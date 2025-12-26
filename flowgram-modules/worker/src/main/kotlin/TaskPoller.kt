package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.akilles.flowgram.models.Task
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Duration
import java.util.Properties
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.KafkaShareConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import kotlin.reflect.KClass


interface TaskPoller<TASK_TYPE : Any> : AutoCloseable {

    /**
     * Invokes <code>consumer</code> callback on every message receive
     */
    fun start(consumer: Consumer<TASK_TYPE>)
}

/**
 * Creates Kafka poller that connects to configured bootstrap servers.
 * [topicToTaskTypeMapping] is specifically used to map which types belong to which topics, as
 * we have 1 topic per task type.
 */
class KafkaTaskPoller(
    val bootstrapServers: List<String>,
    val topicToTaskTypeMapping: Map<String, KClass<out Task>>,
    val groupId: String,
    val objectMapper: ObjectMapper
) : TaskPoller<Task> {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    private var thread: Thread? = null
    private val threadLock = ReentrantLock()

    private interface ThreadOperations : AutoCloseable {
        fun startThread(consumer: Consumer<Task>)
        fun stopThread()
    }

    override fun start(consumer: Consumer<Task>) {
        modifyThread {
            startThread(consumer)
        }
    }

    override fun close() {
        modifyThread {
            stopThread()
        }
    }

    private fun run(taskAction: Consumer<Task>) {
        val consumer = createConsumer()

        val topics = topicToTaskTypeMapping.keys

        consumer.subscribe(topics)
        logger.info { "Subscribed to topics=$topics" }

        while (!Thread.currentThread().isInterrupted) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofMillis(30_000));

            if (!records.isEmpty) {
                logger.info { "Received ${records.count()} records" }

                records.forEach { record ->
                    logger.info { "Record $record" }
                    val taskType = topicToTaskTypeMapping[record.topic()]

                    if (taskType == null) {
                        logger.error { "Could not find task type mapping for topic=${record.topic()}" }
                    } else {
                        try {
                            val task: Task = objectMapper.readValue(record.value(), taskType.java)
                            logger.info { "Processing message with key=${record.key()} and task=${task}" }

                            try {
                                taskAction.accept(task)
                            } catch (exception: Exception) {
                                logger.error(exception) {"Failed to process task $task"}
                            }
                        } catch (exception: Exception) {
                            logger.error(exception) { "Failed to deserialize record with key=${record.key()} value=${record.value()} from topic=${record.topic()}}" }
                        }

                    }
                }
            }
        }
        consumer.close()

        logger.info { "Consumer is exiting" }
    }

    private fun modifyThread(block: ThreadOperations.() -> Unit) {
        val threadOps = object : ThreadOperations {
            init {
                threadLock.lock()
            }

            override fun startThread(consumer: Consumer<Task>) {
                if (thread != null) {
                    logger.info { "Cannot start thread as it's already running" }
                } else {
                    val newThread = Thread { run(consumer) }
                    newThread.start()
                    thread = newThread
                }
            }

            override fun stopThread() {
                if (thread != null) {
                    logger.info { "Stopping running thread" }
                    thread?.interrupt()
                    thread = null
                }
            }

            override fun close() {
                threadLock.unlock()
            }
        }

        threadOps.block()
    }

    private fun createConsumer(): KafkaConsumer<String, String> {
        val properties = Properties()

        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers.joinToString(",")
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        logger.info { "Creating kafka share consumer for $bootstrapServers" }

        return KafkaConsumer<String, String>(properties)
    }
}

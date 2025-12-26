package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.akilles.flowgram.models.Destination
import io.github.akilles.flowgram.models.DestinationType
import io.github.akilles.flowgram.models.FilterType
import io.github.akilles.flowgram.models.MessageFilter
import io.github.akilles.flowgram.models.Task
import java.util.Collections
import java.util.UUID
import java.util.function.Consumer
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class KafkaTaskPollerTest : BaseKafkaTest() {

    val bootstrapServers = kafkaContainer.bootstrapServers
    private val adminClient: AdminClient = AdminClient.create(
        mutableMapOf<String, Any>(
            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers
        )
    )

   private lateinit var producer: KafkaProducer<String, String>;

    private val objectMapper = jacksonObjectMapper()

    private val pollTaskTopic = "tasks.poll"
    private val sendTaskTopic = "tasks.send"

    @BeforeEach
    fun setup() {
        producer = KafkaProducer(
            mutableMapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name
            )
        );

        adminClient.createTopics(
            mutableListOf(
                NewTopic(pollTaskTopic, 1, 1),
                NewTopic(sendTaskTopic, 1, 1),
            )
        )
    }

    @AfterEach
    fun cleanup() {
        adminClient.deleteTopics(mutableListOf(pollTaskTopic, sendTaskTopic))
    }

    @Test
    fun `Test consumes and routes messages`() {
        createPoller().use { poller ->
            val tasks = Collections.synchronizedList(mutableListOf<Task>())
            val taskCollector: Consumer<Task> = Consumer(tasks::add)

            poller.start(taskCollector)

            val dispatchedTasks = listOf(
                Task.PollTask(
                    listOf("source"), listOf(Destination(DestinationType.SEND, 10)),
                    MessageFilter(FilterType.KEYWORD, "filter"), 101
                ),
                Task.SendTask(
                    10, 101, "messageBody"
                )
            )


            dispatchedTasks.forEach(::sendTask)

            Thread.sleep(5_000)

            assertEquals(dispatchedTasks.toSet(), tasks.toSet())
        }
    }

    private fun createPoller() = KafkaTaskPoller(
        listOf(bootstrapServers), mapOf(
            pollTaskTopic to Task.PollTask::class,
            sendTaskTopic to Task.SendTask::class,
        ), "my-group", objectMapper
    )

    private fun sendTask(task: Task) {
        val topic = when (task) {
            is Task.PollTask -> pollTaskTopic
            is Task.SendTask -> sendTaskTopic
        }

        producer.send(
            ProducerRecord(
                topic,
                UUID.randomUUID().toString(),
                objectMapper.writeValueAsString(task)
            )
        )
    }
}

package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.akilles.flowgram.models.Task
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

fun interface TaskPublisher {

    fun publish(task: Task)
}

class KafkaTaskPublisher(
    bootstrapServers: List<String>,
    private val mapper: ObjectMapper
) : TaskPublisher {

    private val producer = KafkaProducer<String, String>(
        mutableMapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers.joinToString(","),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name
        )
    );

    override fun publish(task: Task) {
        when (task) {
            is Task.PollTask -> producer.send(
                ProducerRecord(
                    "tasks.poll", task.workflowId.toString(), mapper.writeValueAsString(task)
                )
            )

            is Task.SendTask -> producer.send(
                ProducerRecord(
                    "tasks.send", task.workflowId.toString(), mapper.writeValueAsString(task)
                )
            )
        }
    }
}

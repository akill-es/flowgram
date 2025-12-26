package io.github.akilles.flowgram.worker

import org.testcontainers.kafka.ConfluentKafkaContainer

abstract class BaseKafkaTest {

    companion object {
        @JvmStatic
        protected val kafkaContainer = ConfluentKafkaContainer("confluentinc/cp-kafka:8.1.1")

        init {
            kafkaContainer.start()
        }
    }
}

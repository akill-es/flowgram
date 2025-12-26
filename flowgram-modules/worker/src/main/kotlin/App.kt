package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.akilles.flowgram.models.Task
import okhttp3.OkHttpClient

fun main() {

    val mapper = jacksonObjectMapper()

    val bootstrapServers = listOf("localhost:9092")
    val poller = KafkaTaskPoller(
        bootstrapServers,
        mapOf(
            "tasks.poll" to Task.PollTask::class,
            "tasks.send" to Task.SendTask::class,
        ),
        "worker-group",
        mapper
    )
    val taskPublisher = KafkaTaskPublisher(bootstrapServers, mapper)
//
//    taskPublisher.publish(
//        Task.PollTask(
//            sources = listOf(Source("shiny_workflow_telegram_bot", "<toke>")),
//            destination = listOf(
//                Destination(DestinationType.SEND, -5188450485L, "<token>")
//            ),
//            filter = MessageFilter(type = FilterType.KEYWORD, "huy"),
//            101
//        )
//    )

    val httpClient = OkHttpClient()

    val worker = Worker(poller, httpClient, mapper, taskPublisher)

    worker.start()
}

package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.akilles.flowgram.models.Task
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.OkHttpClient

private val logger = KotlinLogging.logger {}

fun main() {
    val mapper = jacksonObjectMapper()
    val poller = KafkaTaskPoller(
        listOf("localhost:9092"),
        mapOf(
            "tasks.poll" to Task.PollTask::class,
            "tasks.send" to Task.SendTask::class,
        ),
        "worker-group",
        mapper
    )
    val httpClient = OkHttpClient()

    val worker = Worker(poller, httpClient, mapper)

//    val tgPoller = PollTaskHandler.TelegramPoller(
//        Task.PollTask(
//            listOf(
//                Source(
//                    "shiny_workflow_telegram_bot",
//                    "<token>"
//                )
//            ),
//            emptyList(),
//            MessageFilter(FilterType.KEYWORD, "huy"),
//            100L
//        ),
//        Duration.ofSeconds(15),
//        httpClient,
//        mapper
//    )
//
//    tgPoller.run()
}

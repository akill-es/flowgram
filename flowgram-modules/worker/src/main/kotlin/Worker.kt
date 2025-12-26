package io.github.akilles.flowgram.worker

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.akilles.flowgram.models.Source
import io.github.akilles.flowgram.models.Task
import io.github.oshai.kotlinlogging.KotlinLogging
import java.lang.StringBuilder
import java.time.Duration
import okhttp3.OkHttpClient
import okhttp3.Request


class PollTaskHandler(
    private val httpClient: OkHttpClient,
    private val mapper: ObjectMapper
) {


    class TelegramPoller(
        private val task: Task.PollTask,
        private val pollingInterval: Duration,
        private val httpClient: OkHttpClient,
        private val objectMapper: ObjectMapper
    ) : Runnable {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class TelegramUpdates(
            val ok: Boolean,
            val result: List<TelegramUpdate>
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class TelegramUpdate(
                @JsonProperty("update_id")
                val updateId: Long,
                val message: Message
            ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Message(
                    val from: User,
                    val text: String
                )

                @JsonIgnoreProperties(ignoreUnknown = true)
                data class User(
                    val username: String
                )
            }
        }

        companion object {
            private val logger = KotlinLogging.logger { }
        }

        // we need to store last read offset to be able to advance to the next update
        // after reading message offset, the next one should be +1 in a subsequent request
        private val lastReadMessageOffset: MutableMap<String, Long> = mutableMapOf()

        override fun run() {

            while (!Thread.currentThread().isInterrupted) {
                logger.info { "Starting poller for workflowId=${task.workflowId} $task" }

                val messages = mutableListOf<TelegramUpdates.TelegramUpdate>()

                for (source in task.sources) {
                    messages.addAll(fetchMessages(source))
                }

                if (messages.isNotEmpty()) {
                    logger.info { "Received ${messages.size} for task $task:\n$messages" }
                }

                Thread.sleep(pollingInterval)
            }
        }

        private fun fetchMessages(source: Source): List<TelegramUpdates.TelegramUpdate> {
            val urlBuilder = StringBuilder("https://api.telegram.org/bot${source.botKey}/getUpdates")
            val lastOffset = lastReadMessageOffset[source.botName]

            if (lastOffset != null) {
                urlBuilder.append("?offset=${lastOffset + 1}")
            }

            val request: Request = Request.Builder()
                .url(urlBuilder.toString())
                .build()

            val stringResponse = httpClient.newCall(request).execute().use { response ->
                response.body.string()
            }

            val updates = objectMapper.readValue<TelegramUpdates>(stringResponse).result

            val maxOffset: Long? = updates.maxByOrNull { it.updateId }?.updateId

            maxOffset?.let {
                lastReadMessageOffset[source.botName] = it
            }

            return updates
        }
    }

    private val workflowToPollingThreads: MutableMap<Long, Thread> = mutableMapOf()

    fun process(task: Task.PollTask) {
        // start telegram polling task
        workflowToPollingThreads.computeIfAbsent(task.workflowId) {
            val tgPoller = TelegramPoller(
                task, Duration.ofSeconds(15), httpClient,
                mapper
            )

            val thread = Thread(tgPoller)

            thread.start()

            thread
        }
    }
}

class Worker(
    private val poller: KafkaTaskPoller,
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper
) {

    private val pollTaskHandler = PollTaskHandler(httpClient, objectMapper)

    fun start() {
        poller.start { task ->
            when (task) {
                is Task.PollTask -> pollTaskHandler.process(task)
                is Task.SendTask -> {
                    // ignore for now
                }
            }
        }
        poller.awaitTermination()
    }
}

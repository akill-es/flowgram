package io.github.akilles.flowgram.models

sealed class Task {

    class PollTask(
        val sources: List<String>,
        val destination: List<Destination>,
        val filter: MessageFilter,
        val workflowId: Long
    ) : Task()

    class SendTask(
        val destinationChatId: Long,
        val workflowId: Long,
        val messageBody: String
    ) : Task()
}

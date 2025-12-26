package io.github.akilles.flowgram.models

sealed class Task {

    data class PollTask(
        val sources: List<Source>,
        val destination: List<Destination>,
        val filter: MessageFilter,
        val workflowId: Long
    ) : Task()

    data class SendTask(
        val destinationChatId: Long,
        val botKey: String,
        val workflowId: Long,
        val messageBody: String
    ) : Task()
}

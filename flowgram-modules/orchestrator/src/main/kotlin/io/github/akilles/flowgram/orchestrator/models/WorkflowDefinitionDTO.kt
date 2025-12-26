package io.github.akilles.flowgram.orchestrator.models

import java.util.UUID

data class WorkflowDefinitionDTO(
    val id: UUID,
    val sourcesChatIds: List<Long>,
    val destinationChatIds: List<Long>,
    val botKey: String,
    val filterStrings: List<String>
)


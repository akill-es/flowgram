package io.github.akilles.flowgram.orchestrator.entities

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "workflow_definitions")
data class WorkflowDefinition(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "long_id")
    val longId: Long,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "sources_chat_ids", columnDefinition = "bigint[]")
    val sourcesChatIds: List<Long>,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "destination_chat_ids", columnDefinition = "bigint[]")
    val destinationChatIds: List<Long>,

    @Column(name = "bot_key")
    val botKey: String,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "filter_strings", columnDefinition = "text[]")
    val filterStrings: List<String>
)


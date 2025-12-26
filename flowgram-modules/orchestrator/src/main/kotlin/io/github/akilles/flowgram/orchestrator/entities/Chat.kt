package io.github.akilles.flowgram.orchestrator.entities

import jakarta.persistence.*

@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @Column(name = "chat_id")
    val chatId: Long,

    @Column(name = "chat_name")
    val chatName: String
)


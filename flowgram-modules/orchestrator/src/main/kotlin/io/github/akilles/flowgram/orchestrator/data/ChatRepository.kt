package io.github.akilles.flowgram.orchestrator.data

import io.github.akilles.flowgram.orchestrator.entities.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<Chat, String>


package io.github.akilles.flowgram.orchestrator.services

import io.github.akilles.flowgram.orchestrator.data.ChatRepository
import io.github.akilles.flowgram.orchestrator.entities.Chat as ChatEntity
import io.github.akilles.flowgram.orchestrator.models.ChatDTO
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val repository: ChatRepository
) {

    fun create(dto: ChatDTO): ChatDTO {
        val entity = toEntity(dto)
        val saved = repository.save(entity)
        return toDTO(saved)
    }

    fun getById(chatId: String): ChatDTO? {
        return repository.findById(chatId).map { toDTO(it) }.orElse(null)
    }

    fun getAll(): List<ChatDTO> {
        return repository.findAll().map { toDTO(it) }
    }

    fun update(chatId: String, dto: ChatDTO): ChatDTO? {
        return if (repository.existsById(chatId)) {
            val entity = toEntity(dto)
            val updated = repository.save(entity)
            toDTO(updated)
        } else {
            null
        }
    }

    fun delete(chatId: String): Boolean {
        return if (repository.existsById(chatId)) {
            repository.deleteById(chatId)
            true
        } else {
            false
        }
    }

    private fun toEntity(dto: ChatDTO): ChatEntity {
        return ChatEntity(
            chatId = dto.chatId,
            chatName = dto.chatName
        )
    }

    private fun toDTO(entity: ChatEntity): ChatDTO {
        return ChatDTO(
            chatId = entity.chatId,
            chatName = entity.chatName
        )
    }
}


package io.github.akilles.flowgram.orchestrator.controllers

import io.github.akilles.flowgram.orchestrator.models.ChatDTO
import io.github.akilles.flowgram.orchestrator.services.ChatService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
@Tag(name = "Chats", description = "API for managing chats")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping
    fun create(@RequestBody chat: ChatDTO): ResponseEntity<ChatDTO> {
        val created = chatService.create(chat)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/{chatId}")
    fun getById(@PathVariable chatId: String): ResponseEntity<ChatDTO> {
        val chat = chatService.getById(chatId)
        return if (chat != null) {
            ResponseEntity.ok(chat)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<ChatDTO>> {
        val chats = chatService.getAll()
        return ResponseEntity.ok(chats)
    }

    @PutMapping("/{chatId}")
    fun update(
        @PathVariable chatId: String,
        @RequestBody chat: ChatDTO
    ): ResponseEntity<ChatDTO> {
        val updated = chatService.update(chatId, chat)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{chatId}")
    fun delete(@PathVariable chatId: String): ResponseEntity<Void> {
        val deleted = chatService.delete(chatId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}


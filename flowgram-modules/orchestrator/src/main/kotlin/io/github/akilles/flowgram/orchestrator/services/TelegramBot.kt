package io.github.akilles.flowgram.orchestrator.services

import io.github.akilles.flowgram.orchestrator.models.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramBot(
    @Value("\${telegram.bot.username}")
    private val botUsername: String,

    @Value("\${telegram.bot.token}")
    private val botToken: String,
    
    private val restTemplate: RestTemplate = RestTemplate()
) : TelegramLongPollingBot() {

    override fun getBotUsername(): String = botUsername
    override fun getBotToken(): String = botToken

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage()) return

        val message = update.message
        if (!message.hasText()) return

        // 🔥 Проверяем, что это группа
        if (!message.chat.isGroupChat && !message.chat.isSuperGroupChat) return

        val chatId = message.chatId
        val chatName = message.chat.title
        val text = message.text
        val user = message.from.userName ?: message.from.firstName

        // HTTP POST call to localhost:8080/chat
        try {
            val chat = ChatDTO(chatId = chatId, chatName = chatName ?: "")
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val request = HttpEntity(chat, headers)
            restTemplate.postForObject("http://127.0.0.1:8080/chat", request, String::class.java)
        } catch (e: Exception) {
            // Handle exception if needed
            e.printStackTrace()
        }
    }
}
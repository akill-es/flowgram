package io.github.akilles.flowgram.orchestrator

import io.github.akilles.flowgram.orchestrator.services.TelegramBot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.util.Properties


@SpringBootApplication
class OrchestratorApplication

fun main(args: Array<String>) {
    val props = loadProperties()
    val token = props.getProperty("telegram.bot.token")
    val username = props.getProperty("telegram.bot.username")
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(TelegramBot(username, token))
    runApplication<OrchestratorApplication>(*args)
}

fun loadProperties(): Properties {
    val props = Properties()
    val input = object {}.javaClass.classLoader
        .getResourceAsStream("application.properties")
        ?: error("application.properties not found")

    props.load(input)
    return props
}

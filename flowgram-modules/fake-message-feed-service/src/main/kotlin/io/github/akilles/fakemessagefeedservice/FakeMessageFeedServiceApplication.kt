package io.github.akilles.fakemessagefeedservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FakeMessageFeedServiceApplication

fun main(args: Array<String>) {
    runApplication<FakeMessageFeedServiceApplication>(*args)
}

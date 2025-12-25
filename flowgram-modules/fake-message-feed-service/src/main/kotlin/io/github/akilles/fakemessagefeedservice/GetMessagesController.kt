package io.github.akilles.fakemessagefeedservice

import java.time.Instant
import net.datafaker.Faker
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/get-messages")
class GetMessagesController {

    data class Message(
        val body: String,
        val author: String,
        val timestamp: Instant = Instant.now()
    )


    @GetMapping
    fun getMessages(@RequestParam(name = "count", defaultValue = "2") numberOfMessages: Int): List<Message> {
        return generateMessages(numberOfMessages)
    }

    private fun generateMessages(count: Int): List<Message> {
        val faker = Faker();

        return buildList<Message>(count) {
            repeat(count) {
                add(Message(
                    body = faker.bojackHorseman().quotes(),
                    author = faker.bojackHorseman().characters(),
                ))
            }
        }
    }
}

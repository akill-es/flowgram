package io.github.akilles.fakemessagefeedservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals
import kotlin.test.assertFalse


@WebMvcTest(GetMessagesController::class)
@AutoConfigureRestTestClient
class FakeMessageFeedServiceApplicationTests {


    @Autowired
    lateinit var restTestClient: RestTestClient

    @Autowired
    lateinit var mapper: ObjectMapper


    @Test
    fun `Test Generates Messages`() {
        val count = 10
        val response = restTestClient.get()
            .uri("${FakeMessageFeedServiceApplicationTests.endpoint}?count=${count}")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(String::class.java).returnResult().responseBody ?: error("No messages received")

        val messages = mapper.readValue<List<GetMessagesController.Message>>(response)


        assertEquals(messages.size, count)

        for (message in messages) {
            assertFalse { message.author.isEmpty() }
            assertFalse { message.body.isEmpty() }
        }
    }

    companion object {
        const val endpoint = "/api/v1/get-messages"
    }

}

package com.jinoralen.service.openai

import app.jinoralen.service.openai.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.OpenAITimeoutException
import com.aallam.openai.client.OpenAI
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(BetaOpenAI::class)
@ExperimentalCoroutinesApi
class OpenAiServiceTest {

    private val openAiClient = mockk<OpenAI>()
    private val sut = OpenAiService(openAiClient)

    @Test
    fun `should return summary for specified URL`() = runTest {
        val url = OpenAiUrl("www.wikipedia.org")
        coEvery {
            openAiClient.chatCompletion(
                request = match { it.messages.firstOrNull()?.content?.contains(url.url) == true},
            )
        } returns mockk {
            every { choices } returns listOf(
                mockk(relaxed = true) {
                    every { message?.content } returns "Wikipedia summary"
                }
            )
        }

        val result = sut.getSummaryForUrl(url)

        assertEquals(Result.success(OpenAiSummary("Wikipedia summary")), result)
    }

    @Test
    fun `should fail with no summary available message if there were no choices`() = runTest {
        val url = OpenAiUrl("www.wikipedia.org")
        coEvery {
            openAiClient.chatCompletion(
                request = match { it.messages.firstOrNull()?.content?.contains(url.url) == true},
            )
        } returns mockk { every { choices } returns emptyList() }

        val result = sut.getSummaryForUrl(url)

        assertEquals(Result.failure<OpenAiSummary>(OpenAiNoChoiceException()), result)
    }

    @Test
    fun `should fail with exception if OpenAi client throws`() = runTest {
        val url = OpenAiUrl("www.wikipedia.org")
        val clientException = mockk<OpenAITimeoutException>()
        coEvery {
            openAiClient.chatCompletion(
                request = match { it.messages.firstOrNull()?.content?.contains(url.url) == true},
            )
        } throws clientException

        val result = sut.getSummaryForUrl(url)

        assertEquals(Result.failure<OpenAiSummary>(OpenAiException("OpenAi client failed", clientException)), result)
    }
}

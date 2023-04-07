package app.jinoralen.service.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

@OptIn(BetaOpenAI::class)
class OpenAiService internal constructor(
    private val openAiClient: OpenAI
) {
    constructor(): this(OpenAI(OPEN_AI_TOKEN))

    private val model = ModelId("gpt-3.5-turbo")

    suspend fun getSummaryForUrl(url: OpenAiUrl): Result<OpenAiSummary> {
        val request = ChatCompletionRequest(
            model = model,
            messages = listOf(ChatMessage(ChatRole.User,"tldr ${url.url}"))
        )

        return try {
            openAiClient.chatCompletion(request).toSummary()
        } catch (exception: Exception) {
            Result.failure(OpenAiException("OpenAi client failed", exception))
        }
    }

    private fun ChatCompletion.toSummary(): Result<OpenAiSummary> {
        return choices.firstOrNull()?.message?.content?.let {
            Result.success(OpenAiSummary(it))
        } ?: Result.failure(OpenAiNoChoiceException())
    }
}

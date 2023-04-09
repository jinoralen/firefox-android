package app.jinoralen.feature.tldr.summary

import androidx.lifecycle.ViewModel
import app.jinoralen.service.openai.OpenAiService
import app.jinoralen.service.openai.OpenAiUrl
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container


sealed class SummaryState {
    object Loading: SummaryState()

    data class Data(val summary: String): SummaryState()

    data class Error(val error: String): SummaryState()
}

sealed class SummarySideEffect

class SummaryScreenViewModel(
    private val openAiService: OpenAiService = OpenAiService()
): ViewModel(), ContainerHost<SummaryState, SummarySideEffect> {
    override val container = container<SummaryState, SummarySideEffect>(
        initialState = SummaryState.Loading
    )

    fun getSummaryFor(url: String) = intent {
        reduce { SummaryState.Loading }

        openAiService.getSummaryForUrl(OpenAiUrl(url)).fold(
            onSuccess = { summary -> reduce { SummaryState.Data(summary.text)} },
            onFailure = { exception -> reduce { SummaryState.Error(exception.message ?: "UnknownError") } }
        )
    }
}

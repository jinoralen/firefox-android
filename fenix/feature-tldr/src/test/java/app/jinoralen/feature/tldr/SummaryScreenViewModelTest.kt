package app.jinoralen.feature.tldr

import app.jinoralen.feature.tldr.summary.SummaryScreenViewModel
import app.jinoralen.feature.tldr.summary.SummaryState
import app.jinoralen.service.openai.OpenAiNoChoiceException
import app.jinoralen.service.openai.OpenAiService
import app.jinoralen.service.openai.OpenAiSummary
import app.jinoralen.service.openai.OpenAiUrl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.orbitmvi.orbit.test

@ExperimentalCoroutinesApi
class SummaryScreenViewModelTest {
    val openAiService = mockk<OpenAiService>()
    val sut = SummaryScreenViewModel(openAiService = openAiService)

    @Test
    fun `should init with Loading state`() = runTest {
        val testSubject = sut.test()

        testSubject.assert(SummaryState.Loading)
    }

    @Test
    fun `should show Loading and then Data if request is successful`() = runTest {
        val url = "www.wikipedia.org"
        coEvery {
            openAiService.getSummaryForUrl(OpenAiUrl(url))
        } returns Result.success(OpenAiSummary("Wikipedia summary"))

        val sut = sut.test()
        sut.testIntent { getSummaryFor(url) }
        sut.assert(SummaryState.Loading) {
            states(
                { SummaryState.Loading },
                { SummaryState.Data("Wikipedia summary") }
            )
        }
    }

    @Test
    fun `should show Loading and then Error if request failed`() = runTest {
        val url = "www.wikipedia.org"
        coEvery {
            openAiService.getSummaryForUrl(OpenAiUrl(url))
        } returns Result.failure(OpenAiNoChoiceException())

        val sut = sut.test()
        sut.testIntent { getSummaryFor(url) }
        sut.assert(SummaryState.Loading) {
            states(
                { SummaryState.Loading },
                { SummaryState.Error("No summary available") }
            )
        }
    }
}

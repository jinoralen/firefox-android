package app.jinoralen.feature.wc.session

import androidx.lifecycle.ViewModel
import app.jinoralen.service.wc.WcService
import app.jinoralen.service.wc.model.WcAccount
import app.jinoralen.service.wc.model.WcSession
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container


sealed class SessionScreenState {
    object Loading : SessionScreenState()

    data class Data(
        val sessions: List<WcSession>
    ): SessionScreenState()
}

sealed class SessionSideEffect {
}

class SessionViewModel(
    private val wcService: WcService = WcService,
) : ViewModel(), ContainerHost<SessionScreenState, SessionSideEffect> {
    override val container = container<SessionScreenState, SessionSideEffect>(
        initialState = SessionScreenState.Loading,
    )

    fun loadSessionsInfoFor(pairingTopic: String) = intent {
        val sessions = wcService.getActiveSessionsByPairingTopic(pairingTopic)

        reduce { SessionScreenState.Data(sessions) }
    }
}

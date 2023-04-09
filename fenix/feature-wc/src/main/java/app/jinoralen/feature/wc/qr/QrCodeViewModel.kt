package app.jinoralen.feature.wc.qr

import androidx.lifecycle.ViewModel
import app.jinoralen.service.wc.WcService
import app.jinoralen.service.wc.model.WcChain
import app.jinoralen.service.wc.model.WcSessionEvent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

sealed class QrCodeScreenState {
    object Loading: QrCodeScreenState()

    data class Data(val qrCodeString: String): QrCodeScreenState()
}

sealed class QrCodeScreenSideEffect {
    data class NavigateToAccount(val sessionTopic: String): QrCodeScreenSideEffect()
}

class QrCodeViewModel(
    private val wcService: WcService = WcService
): ViewModel(), ContainerHost<QrCodeScreenState, QrCodeScreenSideEffect> {
    override val container = container<QrCodeScreenState, QrCodeScreenSideEffect>(
        initialState = QrCodeScreenState.Loading,
        onCreate = {
            intent {
                repeatOnSubscription {
                    wcService.observeSessionApproval().collect{
                        when(it){
                            is WcSessionEvent.WcSessionApproved -> postSideEffect(
                                QrCodeScreenSideEffect.NavigateToAccount(it.topic),
                            )
                            WcSessionEvent.WcSessionRejected -> {}
                        }
                    }
                }
            }
        }
    )

    fun createSessionWithNewPairing() = intent {
        wcService.connectTo(listOf(WcChain.POLYGON_MATIC)).fold(
            onSuccess = { reduce { QrCodeScreenState.Data(it.uri) } },
            onFailure = { }
        )
    }

    fun createNewSessionWithExistingPairing(pairingTopic: String) = intent {
        wcService.getActiveSessionsByPairingTopic(pairingTopic).first().let {
            postSideEffect(QrCodeScreenSideEffect.NavigateToAccount(it.topic))
        }
    }
}

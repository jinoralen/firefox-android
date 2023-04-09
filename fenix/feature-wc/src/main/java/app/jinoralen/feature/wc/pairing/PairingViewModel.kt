package app.jinoralen.feature.wc.pairing

import androidx.lifecycle.ViewModel
import app.jinoralen.service.wc.WcService
import app.jinoralen.service.wc.model.WcChain
import app.jinoralen.service.wc.model.WcPairing
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container


sealed class PairingScreenState {
    object Loading: PairingScreenState()

    data class PairingList(val pairings: List<WcPairing>): PairingScreenState()
}

sealed class PairingScreenSideEffect {
    object NavigateToQrCode: PairingScreenSideEffect()
    data class NavigateToSession(val pairingTopic: String): PairingScreenSideEffect()
}

class PairingViewModel(
    private val wcService: WcService = WcService
): ViewModel(), ContainerHost<PairingScreenState, PairingScreenSideEffect> {
    override val container = container<PairingScreenState, PairingScreenSideEffect>(
        initialState = PairingScreenState.Loading,
        onCreate = {
            initWalletState()
        }
    )

    private fun initWalletState() = intent {
        repeatOnSubscription {
            val existingPairing = wcService.getExistingPairings()

            if (existingPairing.isEmpty()) {
                postSideEffect(PairingScreenSideEffect.NavigateToQrCode)
            } else {
                reduce { PairingScreenState.PairingList(existingPairing) }
            }
        }
    }

    fun connectWith(pairing: WcPairing) = intent {
        if(wcService.getActiveSessionsByPairingTopic(pairing.topic).isEmpty()) {
            postSideEffect(PairingScreenSideEffect.NavigateToQrCode)
        } else {
            postSideEffect(PairingScreenSideEffect.NavigateToSession(pairing.topic))
        }
    }
}

package app.jinoralen.feature.wc.account

import androidx.lifecycle.ViewModel
import app.jinoralen.service.wc.WcService
import app.jinoralen.service.wc.model.WcAccount
import app.jinoralen.service.wc.model.WcAccountEvent
import app.jinoralen.service.wc.model.WcSignRequestParams
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

sealed class AccountScreenState {
    object Loading: AccountScreenState()

    data class Data(
        val accounts: List<WcAccount>,
    ): AccountScreenState()
}

sealed class AccountScreenSideEffect {
    data class RequestSuccessful(val json: String): AccountScreenSideEffect()
}


class AccountViewModel(
    private val wcService: WcService = WcService
) : ViewModel(), ContainerHost<AccountScreenState, AccountScreenSideEffect> {
    override val container = container<AccountScreenState, AccountScreenSideEffect>(
        initialState = AccountScreenState.Loading,
        onCreate = {
            intent {
                repeatOnSubscription {
                    wcService.observeAccountEvents().collect{
                        when(it){
                            is WcAccountEvent.SessionRequestErrorResponse -> TODO()
                            is WcAccountEvent.SessionRequestSuccessResponse -> postSideEffect(
                                AccountScreenSideEffect.RequestSuccessful(it.result)
                            )
                        }
                    }
                }
            }
        }
    )

    fun loadAccountsInformationForSessionTopic(sessionTopic: String) = intent {
        val info = wcService.getAccountsInfo(sessionTopic)

        reduce { AccountScreenState.Data(info) }
    }

    fun signMessage(account: WcAccount, message: String) = intent{
        wcService.signRequest(WcSignRequestParams.WcEthSign(
            account = account,
            message = message
        ))
    }
}

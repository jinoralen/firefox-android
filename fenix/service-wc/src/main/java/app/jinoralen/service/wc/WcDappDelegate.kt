package app.jinoralen.service.wc

import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

internal object WcDappDelegate : SignClient.DappDelegate {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _wcEventModels: MutableSharedFlow<Sign.Model> = MutableSharedFlow()
    val wcEventModels: Flow<Sign.Model> =  _wcEventModels.asSharedFlow().onEach {
        Timber.tag(WALLET_CONNECT_TAG).d(it.toString())
    }


    override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
        scope.launch {
            _wcEventModels.emit(approvedSession)
        }
    }

    override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
        scope.launch {
            _wcEventModels.emit(rejectedSession)
        }
    }

    override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
        scope.launch {
            _wcEventModels.emit(updatedSession)
        }
    }

    override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
        scope.launch {
            _wcEventModels.emit(sessionEvent)
        }
    }

    override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
        scope.launch {
            _wcEventModels.emit(deletedSession)
        }
    }

    override fun onSessionExtend(session: Sign.Model.Session) {
        scope.launch {
            _wcEventModels.emit(session)
        }
    }

    override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
        scope.launch {
            _wcEventModels.emit(response)
        }
    }

    override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
        Timber.tag(WALLET_CONNECT_TAG).d( "onConnectionStateChange($state)")
    }

    override fun onError(error: Sign.Model.Error) {
        Timber.tag(WALLET_CONNECT_TAG).d(error.throwable.stackTraceToString())
    }
}

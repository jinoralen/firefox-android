package app.jinoralen.service.wc

import android.app.Application
import app.jinoralen.service.wc.model.*
import com.walletconnect.android.Core
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.android.CoreClient
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@OptIn(FlowPreview::class)
object WcService {
    private val chainMap = WcChain.values().associateBy { it.chainId }

    fun init(application: Application) {
        val serverUri = "wss://$WALLET_CONNECT_PROD_RELAY_URL?projectId=$WALLET_CONNECT_PROJECT_ID"

        CoreClient.initialize(
            relayServerUrl = serverUri,
            connectionType = ConnectionType.AUTOMATIC,
            application = application,
            metaData = Core.Model.AppMetaData(
                name = "Kotlin Dapp Sandbox",
                description = "Sandbox for WC integration",
                url = "example.dapp",
                icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
                redirect = "kotlin-dapp-wc:/request"
            )
        ) { error -> Timber.tag(WALLET_CONNECT_TAG).e(error.throwable.stackTraceToString()) }

        val initParams = Sign.Params.Init(core = CoreClient)

        SignClient.initialize(initParams) { error ->
            Timber.tag(WALLET_CONNECT_TAG).e(error.throwable.stackTraceToString())
        }

        SignClient.setDappDelegate(WcDappDelegate)
    }

    suspend fun getExistingPairings(): List<WcPairing> = withContext(Dispatchers.IO){
        CoreClient.Pairing.getPairings().map {
            WcPairing(
                topic = it.topic,
                name = it.peerAppMetaData?.name ?: "Unknown Pairing"
            )
        }
    }

    suspend fun getActiveSessionsByPairingTopic(topic: String): List<WcSession> = withContext(Dispatchers.IO) {
        SignClient.getListOfActiveSessions()
            .filter { it.pairingTopic == topic }
            .map { session ->
                val accounts = getAccountsFor(session)
                WcSession(session.topic, session.pairingTopic, accounts)
            }
    }

    suspend fun connectTo(chains: List<WcChain>): Result<WcPairingUri> = withContext(Dispatchers.IO) {
        Result.runCatching {
            val pairing = createPairing()

            val params = createConnectParams(pairing, chains)

            connect(params)
        }
    }

    private suspend fun createPairing(): Core.Model.Pairing = suspendCoroutine { continuation ->
        CoreClient.Pairing.create {
            continuation.resumeWithException(it.throwable)
        }?.let {
            continuation.resume(it)
        }
    }

    private fun createConnectParams(pairing: Core.Model.Pairing, chains: List<WcChain>): Sign.Params.Connect {
        val namespaces = mutableMapOf<String, Sign.Model.Namespace.Proposal>()
        val optionalNamespaces = mutableMapOf<String, Sign.Model.Namespace.Proposal>()

        for (chain in chains) {
            when(chain) {
                WcChain.POLYGON_MATIC -> namespaces[chain.chainNamespace] = Sign.Model.Namespace.Proposal(
                    chains = listOf(chain.chainId),
                    methods = chain.methods,
                    events = chain.events
                )

                WcChain.POLYGON_MUMBAI -> namespaces[chain.chainId] = Sign.Model.Namespace.Proposal(
                    methods = chain.methods,
                    events = chain.events
                )
            }
        }

        return Sign.Params.Connect(
            pairing = pairing,
            namespaces = namespaces,
            optionalNamespaces = optionalNamespaces
        )
    }

    private suspend fun connect(params: Sign.Params.Connect): WcPairingUri = suspendCoroutine{ continuation ->
        SignClient.connect(params,
            onSuccess = {
                continuation.resume(WcPairingUri(params.pairing.uri))
            },
            onError = {
                continuation.resumeWithException(it.throwable)
            }
        )
    }

    fun observeSessionApproval(): Flow<WcSessionEvent> {
        return WcDappDelegate.wcEventModels.flatMapConcat { event ->
            when(event) {
                is Sign.Model.ApprovedSession -> flowOf(WcSessionEvent.WcSessionApproved(event.topic))
                is Sign.Model.RejectedSession -> flowOf(WcSessionEvent.WcSessionRejected)

                else -> emptyFlow()
            }
        }
    }

    suspend fun getAccountsInfo(sessionTopic: String) = withContext(Dispatchers.IO) {
        val session = SignClient.getListOfActiveSessions().singleOrNull {
            it.topic == sessionTopic
        } ?: return@withContext emptyList<WcAccount>()

        getAccountsFor(session)
    }

    private fun getAccountsFor(session: Sign.Model.Session): List<WcAccount> {
        val accounts = mutableListOf<WcAccount>()

        for (namespace in session.namespaces.values) {
            for (account in namespace.accounts) {
                val (chainNamespace, chainReference, address) = account.split(":")
                val chainInfo = chainMap["$chainNamespace:$chainReference"]

                if (chainInfo != null) {
                    accounts.add(
                        WcAccount(
                            address = address,
                            chain = chainInfo,
                            sessionTopic = session.topic,
                        ),
                    )
                }
            }
        }

        return accounts
    }

    suspend fun signRequest(params: WcSignRequestParams) = Result.runCatching {
        val requestParams = Sign.Params.Request(
            sessionTopic = params.account.sessionTopic,
            method = params.method,
            params = params.params,
            chainId = params.account.chain.chainId
        )

        sendSignRequest(requestParams)
    }

    private suspend fun sendSignRequest(params: Sign.Params.Request) = suspendCoroutine { continuation ->
        SignClient.request(
            params,
            { continuation.resume(Unit)},
            { continuation.resumeWithException(it.throwable)}
        )
    }

    fun observeAccountEvents(): Flow<WcAccountEvent>{
        return WcDappDelegate.wcEventModels.flatMapConcat { event ->
            when(event) {
                is Sign.Model.SessionRequestResponse -> flowOf(event.toWcAccountEvent())
                else -> emptyFlow()
            }
        }
    }

    private fun Sign.Model.SessionRequestResponse.toWcAccountEvent(): WcAccountEvent =
        when(val response = result) {
            is Sign.Model.JsonRpcResponse.JsonRpcError -> WcAccountEvent.SessionRequestErrorResponse(
                errorMessage = response.message,
                errorCode = response.code
            )
            is Sign.Model.JsonRpcResponse.JsonRpcResult -> WcAccountEvent.SessionRequestSuccessResponse(
                result = response.result
            )
        }
}

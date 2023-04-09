package app.jinoralen.service.wc.model

data class WcAccount(
    val address: String,
    val chain: WcChain,
    val sessionTopic: String,
)

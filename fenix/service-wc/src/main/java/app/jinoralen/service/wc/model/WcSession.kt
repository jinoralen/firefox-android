package app.jinoralen.service.wc.model

data class WcSession(
    val topic: String,
    val pairingTopic: String,
    val accounts: List<WcAccount>
)

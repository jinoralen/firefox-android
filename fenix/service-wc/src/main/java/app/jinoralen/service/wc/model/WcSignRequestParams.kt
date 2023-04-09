package app.jinoralen.service.wc.model

sealed class WcSignRequestParams(
    val account: WcAccount,
    val method: String,
    val params: String,
) {
    class WcEthSign(message: String, account: WcAccount): WcSignRequestParams(
        account = account,
        method = "eth_sign",
        params = "[\"$account.address\", \"$message\"]",
    )
}

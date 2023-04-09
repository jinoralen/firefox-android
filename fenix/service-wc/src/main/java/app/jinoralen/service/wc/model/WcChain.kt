package app.jinoralen.service.wc.model

import androidx.annotation.DrawableRes
import app.jinoralen.service.wc.R

enum class WcChain(
    val chainName: String,
    val chainNamespace: String,
    val chainReference: String,
    @DrawableRes val icon: Int,
    val methods: List<String>,
    val events: List<String>,
    val chainId: String = "$chainNamespace:$chainReference"
) {
    POLYGON_MATIC(
        chainName = "Polygon Matic",
        chainNamespace = "eip155",
        chainReference = "137",
        icon = R.drawable.ic_polygon,
        methods = listOf("eth_sendTransaction", "personal_sign", "eth_sign", "eth_signTypedData"),
        events = listOf("chainChanged", "accountChanged"),
    ),

    POLYGON_MUMBAI(
        chainName = "Polygon Mumbai",
        chainNamespace = "eip155",
        chainReference = "80001",
        icon = R.drawable.ic_polygon,
        methods = listOf("eth_sendTransaction", "personal_sign", "eth_sign", "eth_signTypedData"),
        events = listOf("chainChanged", "accountChanged"),
    ),
}

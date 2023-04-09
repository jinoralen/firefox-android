package app.jinoralen.feature.wc

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.jinoralen.feature.wc.account.AccountScreen
import app.jinoralen.feature.wc.pairing.PairingScreen
import app.jinoralen.feature.wc.qr.QrCodeScreen
import app.jinoralen.feature.wc.session.SessionScreen

@Composable
fun WcScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pairing") {
        composable("pairing") {
            PairingScreen(
                onNavigateToSessionScreen = { pairingTopic ->
                    navController.navigate("sessions/$pairingTopic")
                },
                onClickNewPairing = {
                    navController.navigate("qrCode")
                }
            )
        }

        composable(
            "qrCode?pairingTopic={pairingTopic}",
            arguments = listOf(navArgument("pairingTopic") { nullable = true })
        ) {
            val pairingTopic = it.arguments?.getString("pairingTopic")

            QrCodeScreen(
                pairingTopic = pairingTopic,
                onNavigateToAccount = {sessionTopic ->
                    navController.navigate("account/${sessionTopic}")
                }
            )
        }

        composable(
            "sessions/{pairingTopic}",
            arguments = listOf(navArgument("pairingTopic") { nullable = false })
        ) {
            val pairingTopic = it.arguments?.getString("pairingTopic")

            if (pairingTopic != null) {
                SessionScreen(
                    pairingTopic,
                    onNavigateToAccount = { session ->
                        navController.navigate("account/${session.topic}")
                    }
                )
            }
        }

        composable(
            "account/{sessionTopic}",
            arguments = listOf(navArgument("sessionTopic") { nullable = false })
        ) {
            val sessionTopic = it.arguments?.getString("sessionTopic")

            if (sessionTopic != null)
                AccountScreen(sessionTopic, onBackPressed = {
                    navController.popBackStack("pairing", inclusive = false)
                })
            else
                navController.popBackStack("pairing", inclusive = false)
        }
    }
}

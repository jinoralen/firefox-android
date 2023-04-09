package app.jinoralen.feature.wc.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jinoralen.feature.wc.ui.LoadingContent
import app.jinoralen.service.wc.model.WcSession
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SessionScreen(
    pairingTopic: String,
    onNavigateToAccount: (WcSession) -> Unit,
) {
    val viewModel: SessionViewModel = viewModel()

    LaunchedEffect(key1 = pairingTopic) {
        viewModel.loadSessionsInfoFor(pairingTopic)
    }

    when (val state = viewModel.collectAsState().value) {
        is SessionScreenState.Data -> {
            SessionList(state.sessions, onNavigateToAccount)
        }

        SessionScreenState.Loading -> LoadingContent()
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SessionList(
    sessions: List<WcSession>,
    onNavigateToAccount: (WcSession) -> Unit,
) = LazyColumn {

    items(sessions.size) { index ->
        val item = sessions[index]
        Card(
            modifier = Modifier.clickable {
                onNavigateToAccount(item)
            },
        ) {
            Column {
                Text("Session: ${item.topic}")
                item.accounts.forEach { account ->
                    ListItem(
                        icon = {
                            Image(
                                painter = painterResource(id = account.chain.icon),
                                contentDescription = "",
                            )
                        },
                        text = { Text(account.chain.chainName) },
                        secondaryText = { Text(text = account.address) },
                    )
                }
            }
        }
    }
}

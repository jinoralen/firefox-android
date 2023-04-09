package app.jinoralen.feature.wc.pairing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jinoralen.feature.wc.ui.LoadingContent
import app.jinoralen.service.wc.model.WcPairing
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PairingScreen(
    onNavigateToSessionScreen: (String) -> Unit,
    onClickNewPairing: () -> Unit
) {
    val viewModel: PairingViewModel = viewModel()

    when (val state = viewModel.collectAsState().value) {
        PairingScreenState.Loading -> LoadingContent()

        is PairingScreenState.PairingList -> PairingList(
            state = state,
            onClickExistingPairing = { viewModel.connectWith(it) },
            onClickNewPairing = onClickNewPairing
        )
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PairingScreenSideEffect.NavigateToSession ->
                onNavigateToSessionScreen(sideEffect.pairingTopic)
            PairingScreenSideEffect.NavigateToQrCode -> onClickNewPairing()
        }
    }
}

@Composable
private fun PairingList(
    state: PairingScreenState.PairingList,
    onClickExistingPairing: (WcPairing) -> Unit,
    onClickNewPairing: () -> Unit,
) = Column{
    Text("Scan QR code with wallet")

    LazyColumn(modifier = Modifier.weight(1f)) {
        item {
            Button(onClick = onClickNewPairing) {
                Text("New Paring")
            }
        }

        items(count = state.pairings.size) { index ->
            val pairing = state.pairings[index]
            Card(
                modifier = Modifier
                    .clickable { onClickExistingPairing(pairing) }
                    .padding(16.dp)
            ) {
                Text(pairing.name)
            }
        }
    }

}

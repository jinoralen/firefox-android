package app.jinoralen.feature.wc.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jinoralen.feature.wc.ui.LoadingContent
import app.jinoralen.service.wc.model.WcAccount
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AccountScreen(sessionTopic: String, onBackPressed: () -> Unit) {
    val viewModel: AccountViewModel = viewModel()

    BackHandler(onBack = onBackPressed)

    LaunchedEffect(key1 = sessionTopic) {
        viewModel.loadAccountsInformationForSessionTopic(sessionTopic)
    }

    when (val state = viewModel.collectAsState().value) {
        AccountScreenState.Loading -> LoadingContent()
        is AccountScreenState.Data -> {
            AccountList(state.accounts) { account, msg -> viewModel.signMessage(account, msg) }
        }
    }

    SideEffects(viewModel)
}

@Composable
private fun AccountList(
    accounts: List<WcAccount>,
    onClickSignMessage: (WcAccount, String) -> Unit
) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(accounts.size) { index ->
        val account = accounts[index]
        AccountInfo(account = account, onClickSignMessage = onClickSignMessage)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AccountInfo(
    account: WcAccount,
    onClickSignMessage: (WcAccount, String) -> Unit
) {
    Column {
        ListItem(
            icon = {
                Image(
                    painter = painterResource(id = account.chain.icon),
                    contentDescription = ""
                )
            },
            text = { Text(account.chain.chainName) },
            secondaryText = { Text(text = account.address) },
        )

        SignMessage(onClickSignMessage = {msg ->
            onClickSignMessage(account, msg)
        })
    }
}

@Composable
private fun SignMessage(onClickSignMessage: (String) -> Unit) {
    val message = remember { mutableStateOf("Hello World!") }

    TextField(value = message.value, onValueChange = {
        message.value = it
    })

    Button(onClick = { onClickSignMessage(message.value) }) {
        Text("Sign Message")
    }
}

@Composable
private fun SideEffects(viewModel: AccountViewModel) {
    val dialogText = remember { mutableStateOf<String?>(null) }

    val text = dialogText.value
    if (text != null) {
        AlertDialog(
            onDismissRequest = { dialogText.value = null },
            title = { Text("Response") },
            text = { Text(text) },
            confirmButton = {
                Button(onClick = { dialogText.value = null }) {
                    Text("OK")
                }
            },
        )
    }

    viewModel.collectSideEffect {
        when (it) {
            is AccountScreenSideEffect.RequestSuccessful -> {
                dialogText.value = it.json
            }
        }
    }
}

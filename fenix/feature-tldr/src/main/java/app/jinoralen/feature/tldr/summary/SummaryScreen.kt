package app.jinoralen.feature.tldr.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SummaryScreen(url: String) {
    val vm: SummaryScreenViewModel = viewModel()

    LaunchedEffect(true) {
        vm.getSummaryFor(url)
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(modifier = Modifier
                .padding(bottom = 16.dp)
                .size(50.dp, height = 4.dp))
            Text(
                text = "URL Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when(val state = vm.collectAsState().value) {
                is SummaryState.Data -> SummaryContent(state)
                is SummaryState.Error -> ErrorContent(state = state, onReload = {
                    vm.getSummaryFor(url)
                })
                SummaryState.Loading -> LoadingContent()
            }
        }
    }
}

@Composable
private fun SummaryContent(state: SummaryState.Data) {
    Text(text = state.summary)
}

@Composable
private fun ErrorContent(state: SummaryState.Error, onReload: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onReload,) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.error,
            color = MaterialTheme.colors.error,
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Building summary ...")
    }
}

@Composable
@Preview
fun ContentPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingContent()

            Divider()

            SummaryContent(state = SummaryState.Data("This is summary text"))

            Divider()

            ErrorContent(state = SummaryState.Error("Something went wrong"))
        }
    }
}

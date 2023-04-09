package app.jinoralen.feature.wc.qr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jinoralen.feature.wc.ui.LoadingContent
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun QrCodeScreen(
    pairingTopic: String?,
    onNavigateToAccount: (String) -> Unit
) {
    val vm: QrCodeViewModel = viewModel()

    LaunchedEffect(key1 = pairingTopic) {
        if(pairingTopic != null)
            vm.createNewSessionWithExistingPairing(pairingTopic)
        else
            vm.createSessionWithNewPairing()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when(val state = vm.collectAsState().value) {
            is QrCodeScreenState.Data -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Scan QR code with wallet")
                    QrCode(state.qrCodeString)

                    Text("... or copy-paste pairing URI")
                    CopyToClipboard(text = state.qrCodeString)

                }
            }
            QrCodeScreenState.Loading -> LoadingContent()
        }
    }

    vm.collectSideEffect {
        when(it){
            is QrCodeScreenSideEffect.NavigateToAccount -> onNavigateToAccount(it.sessionTopic)
        }
    }

}

@Composable
fun QrCode(text: String) {
    val bitmap = generateQRCode(text)

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
    )
}

private fun generateQRCode(text: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }

    return bitmap
}

@Composable
fun CopyToClipboard(text: String) {
    val context = LocalContext.current
    IconButton(
        onClick = { copyToClipboard(text, context) },
        content = { Icon(Icons.Default.ContentCopy, contentDescription = "Copy to clipboard") }
    )
}

private fun copyToClipboard(text: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("WC Pairing Uri", text)
    clipboard.setPrimaryClip(clip)
}

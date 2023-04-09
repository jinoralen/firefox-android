package org.mozilla.fenix.tldr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.navArgs
import app.jinoralen.feature.tldr.summary.SummaryScreen
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.mozilla.fenix.theme.FirefoxTheme

class TldrFragment: BottomSheetDialogFragment() {

    val args: TldrFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                FirefoxTheme {
                    val url = args.url
                    if (url != null)
                        SummaryScreen(url)
                }
            }
        }
    }
}

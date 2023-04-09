package org.mozilla.fenix.wc

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import app.jinoralen.feature.wc.WcScreen
import mozilla.components.support.base.feature.UserInteractionHandler
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.library.LibraryPageFragment
import org.mozilla.fenix.theme.FirefoxTheme

class WcFragment: LibraryPageFragment<Unit>(), UserInteractionHandler, MenuProvider {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                FirefoxTheme {
                    Surface {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            WcScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.library_wc))
    }


    override fun onBackPressed(): Boolean {
        return false
    }

    override val selectedItems: Set<Unit> = emptySet()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}

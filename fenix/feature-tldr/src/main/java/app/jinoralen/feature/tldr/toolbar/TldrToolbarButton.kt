package app.jinoralen.feature.tldr.toolbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.jinoralen.feature.tldr.R
import mozilla.components.concept.toolbar.Toolbar

class TldrToolbarButton(
    val showTldr: View.() -> Unit
): Toolbar.Action {
    override fun createView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tldr_button_layout, parent, false)

        return view.apply {
            setOnClickListener {
                showTldr()
            }
        }
    }

    override fun bind(view: View) {
    }

}

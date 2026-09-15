package dev.similin.finlog

import androidx.compose.ui.window.ComposeUIViewController
import dev.similin.finlog.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}

package me.riafy.finlog

import androidx.compose.ui.window.ComposeUIViewController
import me.riafy.finlog.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}

package com.flexify.app.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import com.flexify.app.util.core.Logger

@Composable
actual fun KeepScreenOn() {
	val view = LocalView.current
	DisposableEffect(view) {
		view.keepScreenOn = true
		Logger.i("KeepScreenOn", "keeping the screen on until this composable is disposed")
		onDispose {
			view.keepScreenOn = false
			Logger.i("KeepScreenOn", "disposed")
		}
	}
}

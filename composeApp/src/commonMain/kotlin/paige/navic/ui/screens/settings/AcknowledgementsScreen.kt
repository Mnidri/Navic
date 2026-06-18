package com.flexify.app.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.title_acknowledgements
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.LocalPlatformContext
import com.flexify.app.ui.components.layouts.NestedTopBar

@Composable
fun SettingsAcknowledgementsScreen() {
	val libraries by produceLibraries {
		com.flexify.app.generated.resources.Res.readBytes("files/acknowledgements.json").decodeToString()
	}
	val platformContext = LocalPlatformContext.current
	val hideBack = platformContext.sizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
	Scaffold(
		topBar = {
			NestedTopBar(
				{ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_acknowledgements)) },
				hideBack = hideBack
			)
		}
	) { innerPadding ->
		LibrariesContainer(
			libraries,
			modifier = Modifier
				.fillMaxSize(),
			contentPadding = innerPadding
		)
	}
}

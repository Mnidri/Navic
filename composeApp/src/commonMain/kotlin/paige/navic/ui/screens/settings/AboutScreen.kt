package com.flexify.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.info_app_version
import com.flexify.app.composeapp.generated.resources.title_about
import com.flexify.app.composeapp.generated.resources.title_acknowledgements
import com.flexify.app.composeapp.generated.resources.title_chat
import com.flexify.app.composeapp.generated.resources.title_source
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.LocalPlatformContext
import com.flexify.app.LocalNavStack
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.ChevronForward
import com.flexify.app.ui.components.common.Form
import com.flexify.app.ui.components.common.FormRow
import com.flexify.app.ui.components.layouts.NestedTopBar

@Composable
fun SettingsAboutScreen() {
	@Suppress("DEPRECATION")
	val clipboard = LocalClipboardManager.current
	val uriHandler = LocalUriHandler.current
	val backStack = LocalNavStack.current
	val platformContext = LocalPlatformContext.current
	val hideBack = platformContext.sizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
	Scaffold(
		topBar = {
			NestedTopBar(
				{ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_about)) },
				hideBack = hideBack
			)
		}
	) { innerPadding ->
		Column(
			Modifier
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(top = 12.dp, end = 12.dp, start = 12.dp)
		) {
			Form {
				SelectionContainer {
					val text = buildString {
						append(platformContext.name + "\n")
						append(stringResource(com.flexify.app.generated.resources.Res.string.info_app_version, platformContext.appVersion))
					}
					FormRow(onClick = {
						clipboard.setText(AnnotatedString(text))
					}) {
						Text(text)
					}
				}
			}
			Form {
				FormRow(onClick = {
					uriHandler.openUri("https://github.com/ssalggnikool/Navic")
				}) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.title_source))
					Icon(Icons.Outlined.ChevronForward, null)
				}
				FormRow(onClick = {
					uriHandler.openUri("https://discord.gg/TBcnNX66PH")
				}) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.title_chat))
					Icon(Icons.Outlined.ChevronForward, null)
				}
				FormRow(onClick = dropUnlessResumed {
					backStack.add(Screen.Settings.Acknowledgements)
				}) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.title_acknowledgements))
					Icon(Icons.Outlined.ChevronForward, null)
				}
			}
		}
	}
}

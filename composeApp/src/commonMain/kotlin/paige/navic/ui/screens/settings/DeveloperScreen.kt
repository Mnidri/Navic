package com.flexify.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_cancel
import com.flexify.app.composeapp.generated.resources.action_ok
import com.flexify.app.composeapp.generated.resources.action_test_exception_handler
import com.flexify.app.composeapp.generated.resources.info_exception_handler
import com.flexify.app.composeapp.generated.resources.option_check_for_updates
import com.flexify.app.composeapp.generated.resources.option_custom_headers
import com.flexify.app.composeapp.generated.resources.subtitle_check_for_updates
import com.flexify.app.composeapp.generated.resources.title_confirm
import com.flexify.app.composeapp.generated.resources.title_developer
import com.flexify.app.composeapp.generated.resources.title_logs
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import com.flexify.app.LocalNavStack
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.ChevronForward
import com.flexify.app.ui.components.common.Form
import com.flexify.app.ui.components.common.FormButton
import com.flexify.app.ui.components.common.FormRow
import com.flexify.app.ui.components.dialogs.FormDialog
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.ui.screens.settings.components.SettingSwitchRow
import com.flexify.app.util.core.PlatformType

@Composable
fun SettingsDeveloperScreen() {
	val platformContext = LocalPlatformContext.current
	val backStack = LocalNavStack.current
	var exceptionConfirmationShown by rememberSaveable { mutableStateOf(false) }
	val preferenceManager = koinInject<PreferenceManager>()

	Scaffold(
		topBar = {
			NestedTopBar(
				{ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_developer)) },
				hideBack = platformContext.sizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
			)
		}
	) { innerPadding ->
		CompositionLocalProvider(
			LocalMinimumInteractiveComponentSize provides 0.dp
		) {
			Column(
				Modifier
					.padding(innerPadding)
					.verticalScroll(rememberScrollState())
					.padding(top = 16.dp, end = 16.dp, start = 16.dp)
			) {
				Form {
					if (platformContext.platformType == PlatformType.Android) {
						SettingSwitchRow(
							title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.option_check_for_updates)) },
							subtitle = { Text(stringResource(com.flexify.app.generated.resources.Res.string.subtitle_check_for_updates)) },
							value = preferenceManager.checkForUpdates,
							onSetValue = { preferenceManager.checkForUpdates = it }
						)
					}
					FormRow(
						onClick = dropUnlessResumed {
							backStack.lastOrNull()?.let {
								if (it is Screen.Settings.Developer) {
									backStack.add(Screen.Settings.CustomHeaders)
								}
							}
						}
					) {
						Text(stringResource(com.flexify.app.generated.resources.Res.string.option_custom_headers))
						Icon(Icons.Outlined.ChevronForward, null)
					}
					if (platformContext.platformType == PlatformType.Android) {
						FormRow(
							onClick = dropUnlessResumed {
								backStack.lastOrNull()?.let {
									if (it is Screen.Settings.Developer) {
										backStack.add(Screen.Settings.Logs)
									}
								}
							}
						) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.title_logs))
							Icon(Icons.Outlined.ChevronForward, null)
						}
					}
				}
				Form {
					FormRow(onClick = {
						exceptionConfirmationShown = true
					}) {
						Text(
							text = stringResource(com.flexify.app.generated.resources.Res.string.action_test_exception_handler),
							color = MaterialTheme.colorScheme.error
						)
					}
				}
			}
		}
	}

	if (exceptionConfirmationShown) {
		FormDialog(
			onDismissRequest = { exceptionConfirmationShown = false },
			title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.title_confirm)) },
			content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.info_exception_handler)) },
			buttons = {
				FormButton(
					onClick = {
						exceptionConfirmationShown = false
						throw Error("Testing exception handler")
					},
					color = MaterialTheme.colorScheme.error
				) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.action_ok))
				}
				FormButton(
					onClick = {
						exceptionConfirmationShown = false
					}
				) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.action_cancel))
				}
			},
		)
	}
}

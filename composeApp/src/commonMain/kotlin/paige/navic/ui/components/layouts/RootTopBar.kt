package com.flexify.app.ui.components.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.dropUnlessResumed
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_log_out
import com.flexify.app.composeapp.generated.resources.action_sleep_timer
import com.flexify.app.composeapp.generated.resources.action_sleep_timer_enabled
import com.flexify.app.composeapp.generated.resources.action_view_shares
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalPlatformContext
import com.flexify.app.LocalNavStack
import com.flexify.app.domain.models.settings.NavbarConfig
import com.flexify.app.domain.models.settings.NavbarTab
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.icons.Icons
import com.flexify.app.icons.filled.Settings
import com.flexify.app.icons.outlined.AccountCircle
import com.flexify.app.icons.outlined.Bedtime
import com.flexify.app.icons.outlined.Logout
import com.flexify.app.icons.outlined.Search
import com.flexify.app.icons.outlined.Share
import com.flexify.app.domain.manager.SleepTimerManager
import com.flexify.app.ui.components.common.Dropdown
import com.flexify.app.ui.components.common.DropdownItem
import com.flexify.app.ui.components.sheets.SleepTimerSheet
import com.flexify.app.ui.screens.login.viewmodels.LoginViewModel
import com.flexify.app.ui.screens.settings.viewmodels.NavtabsViewModel
import com.flexify.app.ui.theme.positive
import com.flexify.app.ui.core.UiState
import com.flexify.app.util.core.label

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun RootTopBar(
	title: @Composable () -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
	actions: @Composable RowScope.() -> Unit = {},
) {
	val backStack = LocalNavStack.current
	val navViewModel = koinViewModel<NavtabsViewModel>()
	val viewModel = koinViewModel<LoginViewModel>()

	val navState by navViewModel.state.collectAsState()
	val config = (navState as? UiState.Success)?.data

	MediumFlexibleTopAppBar(
		title = {
			CompositionLocalProvider(
				LocalTextStyle provides when (LocalTextStyle.current) {
					MaterialTheme.typography.headlineMedium -> MaterialTheme.typography.headlineSmall
					else -> MaterialTheme.typography.titleLarge
				}
			) {
				title()
			}
		},
		actions = {
			actions()
			Actions(
				onLogOut = {
					viewModel.logout()
					backStack.clear()
					backStack.add(Screen.Login)
				},
				config = config,
			)
		},
		scrollBehavior = scrollBehavior,
		colors = TopAppBarDefaults.topAppBarColors(
			scrolledContainerColor = MaterialTheme.colorScheme.surface
		),
	)
}

@Composable
private fun Actions(
	onLogOut: () -> Unit,
	config: NavbarConfig?,
) {
	val platformContext = LocalPlatformContext.current
	val backStack = LocalNavStack.current

	val isSearchEnabled = config?.tabs?.any {
		it.id == NavbarTab.Id.SEARCH && it.visible
	} == true

	if (!isSearchEnabled) {
		IconButton(
			onClick = dropUnlessResumed {
				platformContext.clickSound()
				backStack.add(Screen.Search(nested = true))
			}
		) {
			Icon(
				Icons.Outlined.Search,
				contentDescription = null
			)
		}
	}

	IconButton(onClick = dropUnlessResumed {
		platformContext.clickSound()
		backStack.add(Screen.Settings.Root)
	}) {
		Icon(
			Icons.Filled.Settings,
			contentDescription = null
		)
	}

	var expanded by remember { mutableStateOf(false) }
	var sleepTimerSheetOpen by remember { mutableStateOf(false) }
	val sleepTimerManager = koinInject<SleepTimerManager>()
	val sleepTimerLeft = sleepTimerManager.timeLeft

	Box {
		IconButton(onClick = {
			platformContext.clickSound()
			expanded = true
		}) {
			Icon(
				Icons.Outlined.AccountCircle,
				contentDescription = null
			)
		}
		Dropdown(
			expanded = expanded,
			onDismissRequest = { expanded = false }
		) {
			DropdownItem(
				text = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_view_shares)) },
				onClick = dropUnlessResumed {
					expanded = false
					backStack.add(Screen.ShareList)
				},
				leadingIcon = { Icon(Icons.Outlined.Share, null) }
			)

			if (sleepTimerLeft != null) {
				DropdownItem(
					text = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_sleep_timer_enabled, sleepTimerLeft.label()), color = MaterialTheme.colorScheme.positive) },
					onClick = {
						expanded = false
						sleepTimerSheetOpen = true
					},
					leadingIcon = { Icon(Icons.Outlined.Bedtime, null, tint = MaterialTheme.colorScheme.positive) }
				)
			} else {
				DropdownItem(
					text = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_sleep_timer)) },
					onClick = {
						expanded = false
						sleepTimerSheetOpen = true
					},
					leadingIcon = { Icon(Icons.Outlined.Bedtime, null) }
				)
			}

			DropdownItem(
				text = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_log_out)) },
				onClick = {
					expanded = false
					onLogOut()
				},
				leadingIcon = { Icon(Icons.Outlined.Logout, null) }
			)
		}
	}

	if (sleepTimerSheetOpen) {
		SleepTimerSheet(onDismissRequest = { sleepTimerSheetOpen = false })
	}
}

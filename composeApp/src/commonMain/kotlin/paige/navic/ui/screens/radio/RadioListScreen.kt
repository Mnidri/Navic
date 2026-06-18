package com.flexify.app.ui.screens.radio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.title_create_playlist
import navic.composeapp.generated.resources.title_radios
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.settings.BottomBarCollapseMode
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Add
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.snackbars.ErrorSnackbar
import com.flexify.app.ui.components.layouts.ArtGrid
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.components.layouts.RootTopBar
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.screens.radio.components.radioListScreenContent
import com.flexify.app.ui.screens.radio.dialogs.RadioCreateDialog
import com.flexify.app.ui.screens.radio.viewmodels.RadioListViewModel
import com.flexify.app.util.ui.withoutTop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RadioListScreen(
	nested: Boolean
) {
	val platformContext = LocalPlatformContext.current
	val scrollManager = LocalBottomBarScrollManager.current
	val viewModel = koinViewModel<RadioListViewModel>()
	val player = koinInject<MediaPlayerViewModel>()
	val radiosState by viewModel.radiosState.collectAsState()
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
	val slideSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
	val scaleInSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
	var createDialogShown by rememberSaveable { mutableStateOf(false) }
	val preferenceManager = koinInject<PreferenceManager>()

	Scaffold(
		topBar = {
			if (!nested) {
				RootTopBar(
					{ Text(stringResource(Res.string.title_radios)) },
					scrollBehavior
				)
			} else {
				NestedTopBar({ Text(stringResource(Res.string.title_radios)) })
			}
		},
		floatingActionButton = {
			AnimatedContent(
				!scrollManager.isTriggered
					|| preferenceManager.bottomBarCollapseMode == BottomBarCollapseMode.Never,
				transitionSpec = {
					val transformOrigin = TransformOrigin(0f, 1f)
					(slideInHorizontally(slideSpec) { it / 2 }
						+ scaleIn(scaleInSpec, transformOrigin = transformOrigin)
						+ slideInVertically(slideSpec) { it / 2 })
						.togetherWith(slideOutHorizontally(slideSpec) { it / 2 }
							+ scaleOut(transformOrigin = transformOrigin)
							+ slideOutVertically(slideSpec) { it / 2 })
						.using(SizeTransform(clip = false))
				}
			) { notScrolled ->
				if (notScrolled) {
					MediumFloatingActionButton(
						shape = MaterialTheme.shapes.large,
						containerColor = MaterialTheme.colorScheme.primary,
						onClick = {
							platformContext.clickSound()
							createDialogShown = true
						}
					) {
						Icon(
							imageVector = Icons.Outlined.Add,
							contentDescription = stringResource(Res.string.title_create_playlist),
							modifier = Modifier.size(26.dp)
						)
					}
				}
			}
		},
		bottomBar = {
			if (!nested) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { innerPadding ->
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = innerPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			finished = radiosState !is UiState.Loading,
			onRefresh = { viewModel.refreshRadios(true) },
			key = radiosState
		) {
			ArtGrid(
				modifier = if (!nested)
					Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
				else Modifier,
				contentPadding = innerPadding.withoutTop(),
				state = viewModel.gridState,
				verticalArrangement = if ((radiosState as? UiState.Success)?.data?.isEmpty() == true)
					Arrangement.Center
				else Arrangement.spacedBy(12.dp)
			) {
				radioListScreenContent(
					state = radiosState,
					onRadioClick = { radio ->
						player.playRadio(radio)
					}
				)
			}
		}
	}

	ErrorSnackbar(
		error = (radiosState as? UiState.Error)?.error,
		onClearError = { viewModel.clearError() }
	)

	if (createDialogShown) {
		RadioCreateDialog(
			onDismissRequest = { createDialogShown = false },
			onRefresh = { viewModel.refreshRadios(true) }
		)
	}
}

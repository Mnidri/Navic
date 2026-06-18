package com.flexify.app.ui.screens.share

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_no_shares
import navic.composeapp.generated.resources.title_shares
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.settings.BottomBarVisibilityMode
import com.flexify.app.icons.Icons
import com.flexify.app.icons.filled.ShareOff
import com.flexify.app.ui.components.common.ContentUnavailable
import com.flexify.app.ui.components.dialogs.DeletionDialog
import com.flexify.app.ui.components.dialogs.DeletionEndpoint
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.components.layouts.artGridError
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.screens.share.components.ShareListScreenItem
import com.flexify.app.ui.screens.share.viewmodels.ShareListViewModel
import com.flexify.app.util.ui.withoutTop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShareListScreen() {
	val viewModel = koinViewModel<ShareListViewModel>()
	val sharesState by viewModel.sharesState.collectAsState()
	val isRefreshingFlow by viewModel.isRefreshing.collectAsState()
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
	var deletionId by remember { mutableStateOf<String?>(null) }
	val preferenceManager = koinInject<PreferenceManager>()

	Scaffold(
		topBar = { NestedTopBar({ Text(stringResource(Res.string.title_shares)) }) },
		bottomBar = {
			val scrollManager = LocalBottomBarScrollManager.current
			if (preferenceManager.bottomBarVisibilityMode == BottomBarVisibilityMode.AllScreens) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { contentPadding ->
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = contentPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			finished = sharesState !is UiState.Loading && !isRefreshingFlow,
			onRefresh = { viewModel.refreshShares() },
			key = listOf(sharesState, isRefreshingFlow)
		) {
			Crossfade(sharesState) { stateValue ->
				LazyVerticalGrid(
					modifier = Modifier
						.fillMaxSize()
						.nestedScroll(scrollBehavior.nestedScrollConnection),
					columns = GridCells.Fixed(1),
					contentPadding = contentPadding.withoutTop(),
					state = viewModel.gridState,
					verticalArrangement = if ((stateValue as? UiState.Success)?.data?.isEmpty() == true)
						Arrangement.Center
					else Arrangement.Top
				) {
					when (stateValue) {
						is UiState.Loading -> {
							return@LazyVerticalGrid
						}

						is UiState.Error -> artGridError(stateValue)
						is UiState.Success -> {
							items(stateValue.data, { it.id }) { share ->
								ShareListScreenItem(
									modifier = Modifier.animateItem(fadeInSpec = null),
									share = share,
									onSetDeletionId = { newDeletionId ->
										deletionId = newDeletionId
									}
								)
							}
							if (stateValue.data.isEmpty()) {
								item(span = { GridItemSpan(maxLineSpan) }) {
									ContentUnavailable(
										icon = Icons.Filled.ShareOff,
										label = stringResource(Res.string.info_no_shares)
									)
								}
							}
						}
					}
				}
			}
		}
	}

	DeletionDialog(
		endpoint = DeletionEndpoint.SHARE,
		id = deletionId,
		onIdClear = { deletionId = null },
		onRefresh = { viewModel.refreshShares() }
	)
}

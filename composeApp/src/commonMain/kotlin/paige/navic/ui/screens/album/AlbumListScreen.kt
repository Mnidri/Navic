package com.flexify.app.ui.screens.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.title_albums
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.DomainAlbumListType
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.domain.models.settings.BottomBarVisibilityMode
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.snackbars.ErrorSnackbar
import com.flexify.app.ui.components.layouts.ArtGrid
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.components.layouts.RootTopBar
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.screens.album.components.AlbumListScreenSortButton
import com.flexify.app.ui.screens.album.components.albumListScreenContent
import com.flexify.app.ui.screens.album.viewmodels.AlbumListViewModel
import com.flexify.app.ui.screens.share.dialogs.ShareDialog
import com.flexify.app.util.ui.withoutTop
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
	nested: Boolean = false,
	listType: DomainAlbumListType
) {
	val preferenceManager = koinInject<PreferenceManager>()

	val viewModel = koinViewModel<AlbumListViewModel>(
		key = listType.toString(),
		parameters = { parametersOf(listType) }
	)
	val player = koinInject<MediaPlayerViewModel>()
	val selectedSorting by viewModel.listType.collectAsStateWithLifecycle()
	val selectedReversed by viewModel.selectedReversed.collectAsStateWithLifecycle()
	val albumsState by viewModel.albumsState.collectAsStateWithLifecycle()
	val selectedAlbum by viewModel.selectedAlbum.collectAsStateWithLifecycle()
	val starred by viewModel.starred.collectAsStateWithLifecycle()
	val rating by viewModel.rating.collectAsStateWithLifecycle()
	var shareId by remember { mutableStateOf<String?>(null) }
	var shareExpiry by remember { mutableStateOf<Duration?>(null) }
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	val actions: @Composable RowScope.() -> Unit = {
		AlbumListScreenSortButton(
			nested = nested,
			selectedSorting = selectedSorting,
			onSetSorting = { viewModel.setListType(it) },
			selectedReversed = selectedReversed,
			onSetReversed = { viewModel.setReversed(it) }
		)
	}

	Scaffold(
		topBar = {
			if (!nested) {
				RootTopBar(
					{ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_albums)) },
					scrollBehavior,
					actions
				)
			} else {
				NestedTopBar({ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_albums)) }, actions)
			}
		},
		bottomBar = {
			val scrollManager = LocalBottomBarScrollManager.current
			if (!nested || preferenceManager.bottomBarVisibilityMode == BottomBarVisibilityMode.AllScreens) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { innerPadding ->
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = innerPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			finished = albumsState !is UiState.Loading,
			onRefresh = { viewModel.refreshAlbums(true) },
			key = albumsState
		) {
			ArtGrid(
				modifier = if (!nested)
					Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
				else Modifier,
				state = viewModel.gridState,
				contentPadding = innerPadding.withoutTop(),
				verticalArrangement = if ((albumsState as? UiState.Success)?.data?.isEmpty() == true)
					Arrangement.Center
				else Arrangement.spacedBy(12.dp)
			) {
				albumListScreenContent(
					state = albumsState,
					starred = starred,
					selectedAlbum = selectedAlbum,
					selectedAlbumRating = rating,
					onPlayNext = { if (selectedAlbum != null) player.playNext(selectedAlbum as DomainSongCollection) },
					onAddToQueue = { if (selectedAlbum != null) player.addToQueue(selectedAlbum as DomainSongCollection) },
					onUpdateSelection = { viewModel.selectAlbum(it) },
					onClearSelection = { viewModel.clearSelection() },
					onSetShareId = { newShareId ->
						shareId = newShareId
					},
					onSetStarred = { viewModel.starAlbum(it) },
					onRateSelectedAlbum = { viewModel.setRating(it) }
				)
			}
		}
	}

	ErrorSnackbar(
		error = (albumsState as? UiState.Error)?.error,
		onClearError = { viewModel.clearError() }
	)

	ShareDialog(
		id = shareId,
		onIdClear = { shareId = null },
		expiry = shareExpiry,
		onExpiryChange = { shareExpiry = it }
	)
}

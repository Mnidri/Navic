package com.flexify.app.ui.screens.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.dropUnlessResumed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.count_albums
import com.flexify.app.composeapp.generated.resources.title_artists
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.LocalNavStack
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.domain.models.DomainArtist
import com.flexify.app.domain.models.DomainArtistListType
import com.flexify.app.domain.models.settings.BottomBarVisibilityMode
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.snackbars.ErrorSnackbar
import com.flexify.app.ui.components.layouts.ArtGridItem
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.components.layouts.RootTopBar
import com.flexify.app.ui.components.sheets.ArtistSheet
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.ui.screens.artist.components.ArtistListScreenContent
import com.flexify.app.ui.screens.artist.viewmodels.ArtistListViewModel
import com.flexify.app.ui.screens.playlist.dialogs.PlaylistUpdateDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArtistListScreen(
	nested: Boolean = false,
	listType: DomainArtistListType
) {
	val preferenceManager = koinInject<PreferenceManager>()

	val viewModel = koinViewModel<ArtistListViewModel>(
		key = listType.toString(),
		parameters = { parametersOf(listType) }
	)
	val artistsState by viewModel.artistsState.collectAsState()
	val selectedArtist by viewModel.selectedArtist.collectAsState()
	val selectedArtistAlbums by viewModel.selectedArtistAlbums.collectAsState()
	val starred by viewModel.starred.collectAsState()
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	val player = koinInject<MediaPlayerViewModel>()

	Scaffold(
		topBar = {
			if (!nested) {
				RootTopBar({ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_artists)) }, scrollBehavior)
			} else {
				NestedTopBar({ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_artists)) })
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
			finished = artistsState !is UiState.Loading,
			onRefresh = { viewModel.refreshArtists(true) },
			key = artistsState
		) {
			ArtistListScreenContent(
				state = artistsState,
				starred = starred,
				selectedArtist = selectedArtist,
				selectedArtistAlbums = selectedArtistAlbums,
				gridState = viewModel.gridState,
				scrollBehavior = scrollBehavior,
				innerPadding = innerPadding,
				nested = nested,
				onUpdateSelection = { viewModel.selectArtist(it) },
				onClearSelection = { viewModel.clearSelection() },
				onSetStarred = { viewModel.starArtist(it) },
				onPlayNext = { viewModel.playArtistAlbumsNext(player) },
				onAddToQueue = { viewModel.addArtistAlbumsToQueue(player) }
			)
		}
	}

	ErrorSnackbar(
		error = (artistsState as? UiState.Error)?.error,
		onClearError = { viewModel.clearError() }
	)
}

@Composable
fun ArtistsScreenItem(
	modifier: Modifier = Modifier,
	tab: String,
	artist: DomainArtist,
	selected: Boolean,
	selectedArtistAlbums: ImmutableList<DomainAlbum>?,
	starred: Boolean,
	onSelect: () -> Unit,
	onDeselect: () -> Unit,
	onPlayNext: () -> Unit,
	onAddToQueue: () -> Unit,
	onSetStarred: (starred: Boolean) -> Unit
) {
	val platformContext = LocalPlatformContext.current
	val backStack = LocalNavStack.current
	val uriHandler = LocalUriHandler.current

	var playlistDialogShown by rememberSaveable { mutableStateOf(false) }

	Box(modifier) {
		ArtGridItem(
			onClick = dropUnlessResumed {
				platformContext.clickSound()
				backStack.add(Screen.ArtistDetail(artist.id))
			},
			onLongClick = onSelect,
			coverArtId = artist.coverArtId,
			title = artist.name,
			subtitle = pluralStringResource(
				com.flexify.app.generated.resources.Res.plurals.count_albums,
				artist.albumCount,
				artist.albumCount
			),
			id = artist.id,
			tab = tab
		)
		if (selected) {
			ArtistSheet(
				onDismissRequest = onDeselect,
				artist = artist,
				onPlayNext = onPlayNext,
				onAddToQueue = onAddToQueue,
				onAddAllToPlaylist = { playlistDialogShown = true },
				onViewOnLastFm = { 
					onDeselect()
					artist.lastFmUrl?.let { url ->
						uriHandler.openUri(url)
					}
				},
				onViewOnMusicBrainz = { 								
					onDeselect()
					artist.musicBrainzId?.let { id ->
						uriHandler.openUri(
							"https://musicbrainz.org/artist/$id"
						)
					}
				},
				starred = starred,
				onSetStarred = { onSetStarred(!starred) }
			)
		}
		if (playlistDialogShown) {
			PlaylistUpdateDialog(
				songs = selectedArtistAlbums?.flatMap { it.songs }.orEmpty().toPersistentList(),
				onDismissRequest = { playlistDialogShown = false }
			)
		}
	}
}

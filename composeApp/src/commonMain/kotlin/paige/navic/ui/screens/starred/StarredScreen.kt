package com.flexify.app.ui.screens.starred

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.title_starred
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.DomainAlbumListType
import com.flexify.app.domain.models.DomainArtistListType
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.domain.models.DomainSongListType
import com.flexify.app.domain.models.settings.BottomBarVisibilityMode
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.dialogs.QueueDuplicateDialog
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.screens.album.viewmodels.AlbumListViewModel
import com.flexify.app.ui.screens.artist.viewmodels.ArtistListViewModel
import com.flexify.app.ui.screens.share.dialogs.ShareDialog
import com.flexify.app.ui.screens.song.viewmodels.SongListViewModel
import com.flexify.app.ui.screens.starred.components.StarredScreenContent
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StarredScreen() {
	val preferenceManager = koinInject<PreferenceManager>()

	val songsViewModel = koinViewModel<SongListViewModel>(
		key = "starredSongs",
		parameters = { parametersOf(DomainSongListType.Starred) }
	)
	val songsState by songsViewModel.songsState.collectAsStateWithLifecycle()
	val selectedSong by songsViewModel.selectedSong.collectAsStateWithLifecycle()
	val selectedSongIsStarred by songsViewModel.starred.collectAsStateWithLifecycle()
	val selectedSongRating by songsViewModel.selectedSongRating.collectAsStateWithLifecycle()
	val allDownloads by songsViewModel.allDownloads.collectAsStateWithLifecycle()

	val albumsViewModel = koinViewModel<AlbumListViewModel>(
		key = "starredAlbums",
		parameters = { parametersOf(DomainAlbumListType.Starred) }
	)
	val albumsState by albumsViewModel.albumsState.collectAsStateWithLifecycle()
	val selectedAlbum by albumsViewModel.selectedAlbum.collectAsStateWithLifecycle()
	val selectedAlbumIsStarred by albumsViewModel.starred.collectAsStateWithLifecycle()
	val selectedAlbumRating by albumsViewModel.rating.collectAsStateWithLifecycle()

	val artistsViewModel = koinViewModel<ArtistListViewModel>(
		key = "starredArtists",
		parameters = { parametersOf(DomainArtistListType.Starred) }
	)
	val artistsState by artistsViewModel.artistsState.collectAsStateWithLifecycle()
	val selectedArtist by artistsViewModel.selectedArtist.collectAsStateWithLifecycle()
	val selectedArtistAlbums by artistsViewModel.selectedArtistAlbums.collectAsStateWithLifecycle()
	val selectedArtistIsStarred by artistsViewModel.starred.collectAsStateWithLifecycle()

	var shareId by rememberSaveable { mutableStateOf<String?>(null) }
	var shareExpiry by remember { mutableStateOf<Duration?>(null) }

	val player = koinInject<MediaPlayerViewModel>()

	var songToQueue by remember { mutableStateOf<DomainSong?>(null) }

	val isOnline by songsViewModel.isOnline.collectAsStateWithLifecycle()

	Scaffold(
		topBar = { NestedTopBar({ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_starred)) }) },
		bottomBar = {
			val scrollManager = LocalBottomBarScrollManager.current
			if (preferenceManager.bottomBarVisibilityMode == BottomBarVisibilityMode.AllScreens) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { innerPadding ->
		val isAnythingLoading = albumsState is UiState.Loading ||
			artistsState is UiState.Loading || 
			songsState is UiState.Loading
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = innerPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			finished = !isAnythingLoading,
			onRefresh = {
				albumsViewModel.refreshAlbums(true)
				artistsViewModel.refreshArtists(true)
				songsViewModel.refreshSongs(true)
			},
			key = listOf(albumsState, artistsState, songsState)
		) {
			StarredScreenContent(
				innerPadding = innerPadding,
				onSetShareId = { shareId = it },
				isOnline = isOnline,

				songsState = songsState,
				selectedSong = selectedSong,
				allDownloads = allDownloads,
				onPlaySong = { index ->
					player.playNow(songsState.data.orEmpty(), index)
				},
				onSelectSong = {
					songsViewModel.selectSong(it)
				},
				onClearSongSelection = { songsViewModel.clearSelection() },
				selectedSongIsStarred = selectedSongIsStarred,
				onAddSongStar = { songsViewModel.starSong(true) },
				onRemoveSongStar = { songsViewModel.starSong(false) },
				onDownloadSong = { songsViewModel.downloadSong(it) },
				onCancelDownloadSong = { song -> 
					songsViewModel.cancelDownload(song.id)
				},
				onDeleteDownloadSong = { song -> 
					songsViewModel.deleteDownload(song.id) 
				},
				onPlaySongNext = { song ->
					if (player.uiState.value.queue.any { it.id == song.id }) {
						songToQueue = song
					} else {
						player.playNextSingle(song)
					}
				},
				onAddSongToQueue = { song ->
					if (player.uiState.value.queue.any { it.id == song.id }) {
						songToQueue = song
					} else {
						player.addToQueueSingle(song)
					}
				},
				selectedSongRating = selectedSongRating,
				onSetSongRating = { songsViewModel.rateSelectedSong(it) },

				albumsState = albumsState,
				selectedAlbum = selectedAlbum,
				selectedAlbumIsStarred = selectedAlbumIsStarred,
				selectedAlbumRating = selectedAlbumRating,
				onSelectAlbum = { albumsViewModel.selectAlbum(it) },
				onClearAlbumSelection = { albumsViewModel.clearSelection() },
				onStarSelectedAlbum = { albumsViewModel.starAlbum(it) },
				onPlayAlbumNext = { if (selectedAlbum != null) player.playNext(selectedAlbum as DomainSongCollection)},
				onAddAlbumToQueue = { if (selectedAlbum != null) player.addToQueue(selectedAlbum as DomainSongCollection)},
				onRateSelectedAlbum = { albumsViewModel.setRating(it) },

				artistsState = artistsState,
				selectedArtist = selectedArtist,
				selectedArtistAlbums = selectedArtistAlbums,
				selectedArtistIsStarred = selectedArtistIsStarred,
				onSelectArtist = { artistsViewModel.selectArtist(it) },
				onClearArtistSelection = { artistsViewModel.clearSelection() },
				onStarSelectedArtist = { artistsViewModel.starArtist(it) },
				onPlayArtistNext = { if (selectedArtist != null) artistsViewModel.playArtistAlbumsNext(player)},
				onAddArtistToQueue = { if (selectedArtist != null) artistsViewModel.addArtistAlbumsToQueue(player)},
			)
		}
	}

    ShareDialog(
        id = shareId,
        onIdClear = { shareId = null },
        expiry = shareExpiry,
        onExpiryChange = { shareExpiry = it }
    )

	if (songToQueue != null) {
		QueueDuplicateDialog(
			onDismissRequest = { songToQueue = null },
			onConfirm = {
				songToQueue?.let { player.addToQueueSingle(it) }
			}
		)
	}
}

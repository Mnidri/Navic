package com.flexify.app.ui.screens.collection.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.dropUnlessResumed
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject
import com.flexify.app.LocalNavStack
import com.flexify.app.data.database.entities.DownloadStatus
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.domain.models.DomainPlaylist
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.dialogs.QueueDuplicateDialog
import com.flexify.app.ui.components.sheets.SongSheet
import com.flexify.app.ui.screens.playlist.dialogs.PlaylistUpdateDialog

@Composable
fun CollectionDetailScreenSongRowDropdown(
	expanded: Boolean,
	onDismissRequest: () -> Unit,
	onRemoveStar: () -> Unit,
	onAddStar: () -> Unit,
	onShare: () -> Unit,
	collection: DomainSongCollection,
	song: DomainSong,
	onRemoveFromPlaylist: () -> Unit,
	starred: Boolean,
	downloadStatus: DownloadStatus?,
	onDownload: () -> Unit,
	onCancelDownload: () -> Unit,
	onDeleteDownload: () -> Unit,
	onPlayNext: () -> Unit,
	onAddToQueue: () -> Unit,
	rating: Int,
	onSetRating: (Int) -> Unit
) {
	val player = koinInject<MediaPlayerViewModel>()
	val backStack = LocalNavStack.current
	var playlistDialogShown by rememberSaveable { mutableStateOf(false) }

	var isPlayNextPending by rememberSaveable { mutableStateOf<Boolean?>(null) }

	if (expanded) {
		SongSheet(
			onDismissRequest = onDismissRequest,
			song = song,
			collection = collection,
			starred = starred,
			onSetStarred = { starred ->
				if (starred) onAddStar() else onRemoveStar()
			},
			onShare = onShare,
			onPlayNext = {
				if (player.uiState.value.queue.any { it.id == song.id }) {
					isPlayNextPending = true
				} else {
					onPlayNext()
				}
			},
			onAddToQueue = {
				if (player.uiState.value.queue.any { it.id == song.id }) {
					isPlayNextPending = false
				} else {
					onAddToQueue()
				}
			},
			onTrackInfo = dropUnlessResumed {
				backStack.add(Screen.SongDetail(song.id))
			},
			onViewAlbum = if (collection !is DomainAlbum && song.albumId != null) {
				dropUnlessResumed {
					backStack.add(
						Screen.CollectionDetail(
							collectionId = song.albumId,
							tab = "library"
						)
					)
				}
			} else null,
			onViewArtist = dropUnlessResumed {
				backStack.add(Screen.ArtistDetail(song.artistId))
			},
			onAddToPlaylist = {
				playlistDialogShown = true
			},
			onRemoveFromPlaylist = onRemoveFromPlaylist,
			downloadStatus = downloadStatus,
			onDownload = onDownload,
			onCancelDownload = onCancelDownload,
			onDeleteDownload = onDeleteDownload,
			rating = rating,
			onSetRating = onSetRating
		)
	}

	if (playlistDialogShown) {
		PlaylistUpdateDialog(
			songs = persistentListOf(song),
			playlistToExclude = if (collection is DomainPlaylist)
				collection.id
			else null,
			onDismissRequest = { playlistDialogShown = false }
		)
	}

	if (isPlayNextPending != null) {
		QueueDuplicateDialog(
			onDismissRequest = {
				isPlayNextPending = null
				onDismissRequest()
			},
			onConfirm = {
				if (isPlayNextPending == true) onPlayNext() else onAddToQueue()
				isPlayNextPending = null
			}
		)
	}
}

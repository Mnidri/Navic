package com.flexify.app.ui.screens.collection.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.dropUnlessResumed
import kotlinx.collections.immutable.toPersistentList
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.LocalNavStack
import com.flexify.app.data.database.entities.DownloadStatus
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.domain.models.DomainAlbumInfo
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.MoreVert
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.TopBarButton
import com.flexify.app.ui.components.sheets.CollectionSheet
import com.flexify.app.ui.screens.playlist.dialogs.PlaylistUpdateDialog
import com.flexify.app.ui.core.UiState

@Composable
fun CollectionDetailScreenTopBar(
	collection: DomainSongCollection?,
	albumInfoState: UiState<DomainAlbumInfo>,
	titleAlpha: Float,
	onSetShareId: (shareId: String?) -> Unit,
	onDownloadAll: () -> Unit,
	onCancelDownloadAll: () -> Unit,
	onPlayNext: () -> Unit,
	onAddToQueue: () -> Unit,
	downloadStatus: DownloadStatus,
	rating: Int?,
	onSetRating: ((Int) -> Unit)?,
	starred: Boolean?,
	onSetStarred: ((Boolean) -> Unit)? = null,
	refreshCollection: () -> Unit
) {
	val uriHandler = LocalUriHandler.current
	var playlistDialogShown by rememberSaveable { mutableStateOf(false) }
	val backStack = LocalNavStack.current

	NestedTopBar(
		title = {
			Text(
				text = collection?.name.orEmpty(),
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				modifier = Modifier.alpha(titleAlpha)
			)
		},
		actions = {
			Box {
				var expanded by remember { mutableStateOf(false) }
				TopBarButton({
					expanded = true
					refreshCollection()
				}) {
					Icon(
						Icons.Outlined.MoreVert,
						stringResource(com.flexify.app.generated.resources.Res.string.action_more)
					)
				}
				if (expanded) {
					CollectionSheet(
						onDismissRequest = { expanded = false },
						collection = collection,
						albumInfo = (albumInfoState as? UiState.Success)?.data,
						onDownloadAll = onDownloadAll,
						onCancelDownloadAll = onCancelDownloadAll,
						downloadStatus = downloadStatus,
						onShare = { onSetShareId(collection?.id) },
						onPlayNext = onPlayNext,
						onAddToQueue = onAddToQueue,
						onAddAllToPlaylist = { playlistDialogShown = true },
						onViewOnLastFm = { url -> uriHandler.openUri(url) },
						onViewOnMusicBrainz = { id ->
							uriHandler.openUri("https://musicbrainz.org/release/$id")
						},
						onViewArtist =
							if (collection is DomainAlbum)
								dropUnlessResumed { backStack.add(Screen.ArtistDetail(collection.artistId)) }
							else null,
						rating = rating,
						onSetRating = onSetRating,
						starred = starred,
						onSetStarred = if (onSetStarred != null && starred != null) { { onSetStarred(!starred) } } else null
					)
				}
			}
		}
	)

	if (playlistDialogShown) {
		PlaylistUpdateDialog(
			songs = collection?.songs.orEmpty().toPersistentList(),
			onDismissRequest = { playlistDialogShown = false }
		)
	}
}

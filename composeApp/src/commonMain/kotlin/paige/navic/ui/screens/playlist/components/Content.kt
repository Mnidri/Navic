package com.flexify.app.ui.screens.playlist.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Modifier
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.info_no_playlists_short
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.domain.models.DomainPlaylist
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.PlaylistRemove
import com.flexify.app.ui.components.common.ContentUnavailable
import com.flexify.app.ui.components.layouts.artGridPlaceholder
import com.flexify.app.ui.core.UiState

fun LazyGridScope.playlistListScreenContent(
	state: UiState<List<DomainPlaylist>>,
	selectedPlaylist: DomainPlaylist?,
	onUpdateSelection: (DomainPlaylist) -> Unit,
	onClearSelection: () -> Unit,
	onSetShareId: (String) -> Unit,
	onSetDeletionId: (String) -> Unit,
	onPlayNext: () -> Unit,
	onAddToQueue: () -> Unit,
) {
	val data = state.data.orEmpty()
	if (data.isNotEmpty()) {
		items(data, { it.id }) { playlist ->
			PlaylistListScreenItem(
				modifier = Modifier.animateItem(),
				tab = "playlists",
				playlist = playlist,
				selected = playlist == selectedPlaylist,
				onSelect = { onUpdateSelection(playlist) },
				onDeselect = { onClearSelection() },
				onSetShareId = onSetShareId,
				onSetDeletionId = onSetDeletionId,
				onPlayNext = onPlayNext,
				onAddToQueue = onAddToQueue,
			)
		}
	} else {
		when (state) {
			is UiState.Loading -> {
				artGridPlaceholder()
			}

			else -> {
				item(span = { GridItemSpan(maxLineSpan) }) {
					ContentUnavailable(
						icon = Icons.Outlined.PlaylistRemove,
						label = stringResource(com.flexify.app.generated.resources.Res.string.info_no_playlists_short)
					)
				}
			}
		}
	}
}

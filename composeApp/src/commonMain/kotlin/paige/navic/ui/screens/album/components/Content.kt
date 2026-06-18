package com.flexify.app.ui.screens.album.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Modifier
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_no_albums
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Album
import com.flexify.app.ui.components.common.ContentUnavailable
import com.flexify.app.ui.components.layouts.artGridPlaceholder
import com.flexify.app.ui.core.UiState

fun LazyGridScope.albumListScreenContent(
	state: UiState<List<DomainAlbum>>,
	starred: Boolean,
	selectedAlbum: DomainAlbum?,
	selectedAlbumRating: Int,
	onUpdateSelection: (DomainAlbum) -> Unit,
	onClearSelection: () -> Unit,
	onSetShareId: (String) -> Unit,
	onSetStarred: (Boolean) -> Unit,
	onPlayNext: () -> Unit,
	onAddToQueue: () -> Unit,
	onRateSelectedAlbum: (Int) -> Unit
) {
	val data = state.data.orEmpty()
	if (data.isNotEmpty()) {
		items(data, { it.id }) { album ->
			AlbumListScreenItem(
				modifier = Modifier.animateItem(),
				tab = "albums",
				album = album,
				selected = album == selectedAlbum,
				starred = starred,
				onSelect = { onUpdateSelection(album) },
				onDeselect = { onClearSelection() },
				onSetStarred = { onSetStarred(it) },
				onSetShareId = onSetShareId,
				onPlayNext = onPlayNext,
				onAddToQueue = onAddToQueue,
				rating = selectedAlbumRating,
				onSetRating = onRateSelectedAlbum
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
						icon = Icons.Outlined.Album,
						label = stringResource(Res.string.info_no_albums)
					)
				}
			}
		}
	}
}

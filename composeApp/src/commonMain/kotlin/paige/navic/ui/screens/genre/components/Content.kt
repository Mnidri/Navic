package com.flexify.app.ui.screens.genre.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Modifier
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_no_genres
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.domain.models.DomainGenre
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Genre
import com.flexify.app.ui.components.common.ContentUnavailable
import com.flexify.app.ui.core.UiState

fun LazyGridScope.genreListScreenContent(
	state: UiState<List<DomainGenre>>
) {
	val data = state.data.orEmpty()
	if (data.isNotEmpty()) {
		items(data, { it.name }) { genre ->
			GenreListScreenCard(
				modifier = Modifier.animateItem(),
				genre = genre
			)
		}
	} else {
		when (state) {
			is UiState.Loading -> items(10) {
				GenreListScreenCardPlaceholder()
			}

			else -> {
				item(span = { GridItemSpan(maxLineSpan) }) {
					ContentUnavailable(
						icon = Icons.Outlined.Genre,
						label = stringResource(Res.string.info_no_genres)
					)
				}
			}
		}
	}
}

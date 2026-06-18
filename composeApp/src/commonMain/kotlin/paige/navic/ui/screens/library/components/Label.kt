package com.flexify.app.ui.screens.library.components

import androidx.compose.runtime.Composable
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_sort_alphabetical_by_artist
import com.flexify.app.composeapp.generated.resources.option_sort_alphabetical_by_name
import com.flexify.app.composeapp.generated.resources.option_sort_by_year
import com.flexify.app.composeapp.generated.resources.option_sort_downloaded
import com.flexify.app.composeapp.generated.resources.option_sort_frequent
import com.flexify.app.composeapp.generated.resources.option_sort_newest
import com.flexify.app.composeapp.generated.resources.option_sort_random
import com.flexify.app.composeapp.generated.resources.option_sort_rating
import com.flexify.app.composeapp.generated.resources.option_sort_recent
import com.flexify.app.composeapp.generated.resources.option_sort_starred
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.domain.models.DomainAlbumListType

@Composable
fun DomainAlbumListType.label() =
	when (this) {
		DomainAlbumListType.Random -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_random)
		DomainAlbumListType.Newest -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_newest)
		DomainAlbumListType.Frequent -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_frequent)
		DomainAlbumListType.Recent -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_recent)
		DomainAlbumListType.AlphabeticalByName -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_alphabetical_by_name)
		DomainAlbumListType.AlphabeticalByArtist -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_alphabetical_by_artist)
		DomainAlbumListType.Highest -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_rating)
		DomainAlbumListType.Starred -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_starred)
		DomainAlbumListType.Downloaded -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_downloaded)
		DomainAlbumListType.ByYear -> stringResource(com.flexify.app.generated.resources.Res.string.option_sort_by_year)
		else -> "$this"
	}

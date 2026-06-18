package com.flexify.app.domain.models

import androidx.compose.runtime.Immutable
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_sort_by_year
import com.flexify.app.composeapp.generated.resources.option_sort_downloaded
import com.flexify.app.composeapp.generated.resources.option_sort_frequent
import com.flexify.app.composeapp.generated.resources.option_sort_newest
import com.flexify.app.composeapp.generated.resources.option_sort_random
import com.flexify.app.composeapp.generated.resources.option_sort_rating
import com.flexify.app.composeapp.generated.resources.option_sort_starred
import org.jetbrains.compose.resources.StringResource

@Immutable
enum class DomainSongListType(val displayName: StringResource) {
	FrequentlyPlayed(com.flexify.app.generated.resources.Res.string.option_sort_frequent),
	Newest(com.flexify.app.generated.resources.Res.string.option_sort_newest),
	Starred(com.flexify.app.generated.resources.Res.string.option_sort_starred),
	Random(com.flexify.app.generated.resources.Res.string.option_sort_random),
	Downloaded(com.flexify.app.generated.resources.Res.string.option_sort_downloaded),
	Rating(com.flexify.app.generated.resources.Res.string.option_sort_rating),
	Year(com.flexify.app.generated.resources.Res.string.option_sort_by_year)
}

package com.flexify.app.domain.models

import androidx.compose.runtime.Immutable
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_sort_downloaded
import com.flexify.app.composeapp.generated.resources.option_sort_playlist_by_name
import com.flexify.app.composeapp.generated.resources.option_sort_playlist_date_added
import com.flexify.app.composeapp.generated.resources.option_sort_playlist_duration
import com.flexify.app.composeapp.generated.resources.option_sort_random
import org.jetbrains.compose.resources.StringResource

@Immutable
enum class DomainPlaylistListType(val displayName: StringResource) {
	Name(com.flexify.app.generated.resources.Res.string.option_sort_playlist_by_name),
	DateAdded(com.flexify.app.generated.resources.Res.string.option_sort_playlist_date_added),
	Duration(com.flexify.app.generated.resources.Res.string.option_sort_playlist_duration),
	Random(com.flexify.app.generated.resources.Res.string.option_sort_random),
	Downloaded(com.flexify.app.generated.resources.Res.string.option_sort_downloaded)
}

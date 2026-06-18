package com.flexify.app.domain.models

import androidx.compose.runtime.Immutable
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_sort_alphabetical_by_name
import com.flexify.app.composeapp.generated.resources.option_sort_random
import com.flexify.app.composeapp.generated.resources.option_sort_starred
import org.jetbrains.compose.resources.StringResource

@Immutable
enum class DomainArtistListType(val displayName: StringResource) {
	AlphabeticalByName(com.flexify.app.generated.resources.Res.string.option_sort_alphabetical_by_name),
	Starred(com.flexify.app.generated.resources.Res.string.option_sort_starred),
	Random(com.flexify.app.generated.resources.Res.string.option_sort_random)
}

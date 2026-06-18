package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_now_playing_background_style_dynamic
import com.flexify.app.composeapp.generated.resources.option_now_playing_background_style_static
import org.jetbrains.compose.resources.StringResource

enum class NowPlayingBackgroundStyle(val displayName: StringResource) {
	Static(com.flexify.app.generated.resources.Res.string.option_now_playing_background_style_static),
	Dynamic(com.flexify.app.generated.resources.Res.string.option_now_playing_background_style_dynamic)
}

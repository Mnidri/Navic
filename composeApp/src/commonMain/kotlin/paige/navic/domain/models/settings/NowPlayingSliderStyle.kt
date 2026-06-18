package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_now_playing_slider_style_flat
import com.flexify.app.composeapp.generated.resources.option_now_playing_slider_style_slim
import com.flexify.app.composeapp.generated.resources.option_now_playing_slider_style_squiggly
import com.flexify.app.composeapp.generated.resources.option_now_playing_slider_style_yoyo
import org.jetbrains.compose.resources.StringResource

enum class NowPlayingSliderStyle(val displayName: StringResource) {
	Flat(com.flexify.app.generated.resources.Res.string.option_now_playing_slider_style_flat),
	Squiggly(com.flexify.app.generated.resources.Res.string.option_now_playing_slider_style_squiggly),
	Slim(com.flexify.app.generated.resources.Res.string.option_now_playing_slider_style_slim),
	Yoyo(com.flexify.app.generated.resources.Res.string.option_now_playing_slider_style_yoyo)
}

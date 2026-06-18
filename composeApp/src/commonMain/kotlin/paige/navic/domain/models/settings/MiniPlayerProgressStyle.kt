package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_mini_player_progress_style_hidden
import com.flexify.app.composeapp.generated.resources.option_mini_player_progress_style_seekable
import com.flexify.app.composeapp.generated.resources.option_mini_player_progress_style_visible
import org.jetbrains.compose.resources.StringResource

enum class MiniPlayerProgressStyle(val displayName: StringResource) {
	Hidden(com.flexify.app.generated.resources.Res.string.option_mini_player_progress_style_hidden),
	Visible(com.flexify.app.generated.resources.Res.string.option_mini_player_progress_style_visible),
	Seekable(com.flexify.app.generated.resources.Res.string.option_mini_player_progress_style_seekable)
}

package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_mini_player_style_detached
import com.flexify.app.composeapp.generated.resources.option_mini_player_style_unified
import org.jetbrains.compose.resources.StringResource

enum class MiniPlayerStyle(val displayName: StringResource) {
	Unified(com.flexify.app.generated.resources.Res.string.option_mini_player_style_unified),
	Detached(com.flexify.app.generated.resources.Res.string.option_mini_player_style_detached)
}

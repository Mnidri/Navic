package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_animation_style_expressive
import com.flexify.app.composeapp.generated.resources.option_animation_style_standard
import org.jetbrains.compose.resources.StringResource

enum class AnimationStyle(val displayName: StringResource) {
	Expressive(com.flexify.app.generated.resources.Res.string.option_animation_style_expressive),
	Standard(com.flexify.app.generated.resources.Res.string.option_animation_style_standard)
}

package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.theme_mode_dark
import com.flexify.app.composeapp.generated.resources.theme_mode_light
import com.flexify.app.composeapp.generated.resources.theme_mode_system
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode(val title: StringResource) {
	System(com.flexify.app.generated.resources.Res.string.theme_mode_system),
	Dark(com.flexify.app.generated.resources.Res.string.theme_mode_dark),
	Light(com.flexify.app.generated.resources.Res.string.theme_mode_light)
}

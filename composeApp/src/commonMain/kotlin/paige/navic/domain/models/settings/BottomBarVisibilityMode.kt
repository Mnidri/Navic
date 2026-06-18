package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_bottom_bar_visibility_mode_all_screens
import com.flexify.app.composeapp.generated.resources.option_bottom_bar_visibility_mode_default
import org.jetbrains.compose.resources.StringResource

enum class BottomBarVisibilityMode(val displayName: StringResource) {
	Default(com.flexify.app.generated.resources.Res.string.option_bottom_bar_visibility_mode_default),
	AllScreens(com.flexify.app.generated.resources.Res.string.option_bottom_bar_visibility_mode_all_screens)
}

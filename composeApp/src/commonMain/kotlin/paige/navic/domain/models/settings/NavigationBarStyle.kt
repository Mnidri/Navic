package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_navigation_bar_style_normal
import com.flexify.app.composeapp.generated.resources.option_navigation_bar_style_short
import org.jetbrains.compose.resources.StringResource

enum class NavigationBarStyle(val displayName: StringResource) {
	Normal(com.flexify.app.generated.resources.Res.string.option_navigation_bar_style_normal),
	Short(com.flexify.app.generated.resources.Res.string.option_navigation_bar_style_short)
}

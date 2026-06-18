package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_bottom_bar_collapse_mode_never
import com.flexify.app.composeapp.generated.resources.option_bottom_bar_collapse_mode_on_scroll
import org.jetbrains.compose.resources.StringResource

enum class BottomBarCollapseMode(val displayName: StringResource) {
	Never(com.flexify.app.generated.resources.Res.string.option_bottom_bar_collapse_mode_never),
	OnScroll(com.flexify.app.generated.resources.Res.string.option_bottom_bar_collapse_mode_on_scroll)
}

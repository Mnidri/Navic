package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_position_bottom
import com.flexify.app.composeapp.generated.resources.option_position_top
import org.jetbrains.compose.resources.StringResource

enum class ToolbarPosition(val displayName: StringResource) {
	Top(com.flexify.app.generated.resources.Res.string.option_position_top),
	Bottom(com.flexify.app.generated.resources.Res.string.option_position_bottom)
}

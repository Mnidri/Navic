package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_navigation_bar_label_visibility_always
import com.flexify.app.composeapp.generated.resources.option_navigation_bar_label_visibility_only_selected
import org.jetbrains.compose.resources.StringResource

enum class NavigationBarLabelVisibility(val displayName: StringResource) {
	Always(com.flexify.app.generated.resources.Res.string.option_navigation_bar_label_visibility_always),
	OnlySelected(com.flexify.app.generated.resources.Res.string.option_navigation_bar_label_visibility_only_selected)
}

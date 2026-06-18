package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_offline_mode_auto
import com.flexify.app.composeapp.generated.resources.option_offline_mode_forced
import com.flexify.app.composeapp.generated.resources.option_offline_mode_no_wifi
import org.jetbrains.compose.resources.StringResource

enum class OfflineMode(val displayName: StringResource) {
	Auto(com.flexify.app.generated.resources.Res.string.option_offline_mode_auto),
	Forced(com.flexify.app.generated.resources.Res.string.option_offline_mode_forced),
	NoWiFi(com.flexify.app.generated.resources.Res.string.option_offline_mode_no_wifi),
}

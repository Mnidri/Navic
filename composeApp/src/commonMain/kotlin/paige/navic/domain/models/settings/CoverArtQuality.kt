package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.option_quality_high
import com.flexify.app.composeapp.generated.resources.option_quality_low
import com.flexify.app.composeapp.generated.resources.option_quality_medium
import org.jetbrains.compose.resources.StringResource

enum class CoverArtQuality(
	val displayName: StringResource,
	val value: Int
) {
	Low(com.flexify.app.generated.resources.Res.string.option_quality_low, 512),
	Medium(com.flexify.app.generated.resources.Res.string.option_quality_medium, 1024),
	High(com.flexify.app.generated.resources.Res.string.option_quality_high, 4096)
}

package com.flexify.app.domain.models.settings

import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.info_album_replay_gain
import com.flexify.app.composeapp.generated.resources.info_track_replay_gain
import com.flexify.app.composeapp.generated.resources.option_off
import org.jetbrains.compose.resources.StringResource

enum class ReplayGainMode(val displayName: StringResource) {
	Off(com.flexify.app.generated.resources.Res.string.option_off),
	Track(com.flexify.app.generated.resources.Res.string.info_track_replay_gain),
	Album(com.flexify.app.generated.resources.Res.string.info_album_replay_gain)
}

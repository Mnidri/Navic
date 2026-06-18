package com.flexify.app.ui.screens.song

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentMapOf
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.info_album_replay_gain
import com.flexify.app.composeapp.generated.resources.info_track_album
import com.flexify.app.composeapp.generated.resources.info_track_artist
import com.flexify.app.composeapp.generated.resources.info_track_bit_depth
import com.flexify.app.composeapp.generated.resources.info_track_bitrate
import com.flexify.app.composeapp.generated.resources.info_track_channel_count
import com.flexify.app.composeapp.generated.resources.info_track_disc_number
import com.flexify.app.composeapp.generated.resources.info_track_duration
import com.flexify.app.composeapp.generated.resources.info_track_file_size
import com.flexify.app.composeapp.generated.resources.info_track_format
import com.flexify.app.composeapp.generated.resources.info_track_genre
import com.flexify.app.composeapp.generated.resources.info_track_name
import com.flexify.app.composeapp.generated.resources.info_track_number
import com.flexify.app.composeapp.generated.resources.info_track_path
import com.flexify.app.composeapp.generated.resources.info_track_replay_gain
import com.flexify.app.composeapp.generated.resources.info_track_replay_gain_effective
import com.flexify.app.composeapp.generated.resources.info_track_sampling_rate
import com.flexify.app.composeapp.generated.resources.info_track_year
import com.flexify.app.composeapp.generated.resources.info_unknown
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.ui.components.common.Form
import com.flexify.app.ui.components.common.FormRow
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.screens.song.viewmodels.SongDetailViewModel
import com.flexify.app.util.core.effectiveGain
import com.flexify.app.util.core.toFileSize
import com.flexify.app.util.core.toHoursMinutesSeconds

@Composable
fun SongDetailScreen(songId: String) {
	val viewModel = koinViewModel<SongDetailViewModel>(
		key = songId,
		parameters = { parametersOf(songId) }
	)

	val songState by viewModel.songState.collectAsStateWithLifecycle()
	val song = songState.data

	val preferenceManager = koinInject<PreferenceManager>()
	val info = remember(song) {
		song?.let {
			persistentMapOf(
				com.flexify.app.generated.resources.Res.string.info_track_name to song.title,
				com.flexify.app.generated.resources.Res.string.info_track_artist to song.artistName,
				com.flexify.app.generated.resources.Res.string.info_track_album to song.albumTitle,

				com.flexify.app.generated.resources.Res.string.info_track_number to song.trackNumber,
				com.flexify.app.generated.resources.Res.string.info_track_disc_number to song.discNumber,
				com.flexify.app.generated.resources.Res.string.info_track_year to song.year,
				com.flexify.app.generated.resources.Res.string.info_track_genre to song.genre,

				com.flexify.app.generated.resources.Res.string.info_track_duration to song.duration.toHoursMinutesSeconds(),
				com.flexify.app.generated.resources.Res.string.info_track_format to song.mimeType,
				com.flexify.app.generated.resources.Res.string.info_track_bitrate to song.bitRate?.let { "$it kbps" },
				com.flexify.app.generated.resources.Res.string.info_track_bit_depth to song.bitDepth,
				com.flexify.app.generated.resources.Res.string.info_track_sampling_rate to song.sampleRate?.let { "$it Hz" },
				com.flexify.app.generated.resources.Res.string.info_track_channel_count to song.audioChannelCount,

				com.flexify.app.generated.resources.Res.string.info_track_file_size to song.fileSize.toFileSize(),
				com.flexify.app.generated.resources.Res.string.info_track_path to song.filePath,

				com.flexify.app.generated.resources.Res.string.info_track_replay_gain to song.replayGain?.trackGain?.let { "$it dB" },
				com.flexify.app.generated.resources.Res.string.info_album_replay_gain to song.replayGain?.albumGain?.let { "$it dB" },
				com.flexify.app.generated.resources.Res.string.info_track_replay_gain_effective to song.replayGain?.effectiveGain(preferenceManager.replayGainMode)
			)
		}.orEmpty()
	}

	Scaffold(
		topBar = { NestedTopBar({ Text(song?.title.orEmpty()) }) }
	) { contentPadding ->
		Column(
			Modifier
				.verticalScroll(rememberScrollState())
				.padding(
					top = contentPadding.calculateTopPadding() + 12.dp,
					start = 12.dp,
					end = 12.dp
				)
		) {
			Form {
				info.forEach { (key, value) ->
					FormRow {
						Column(Modifier.padding(vertical = 4.dp)) {
							Text(
								text = stringResource(key),
								style = MaterialTheme.typography.labelMedium,
								color = MaterialTheme.colorScheme.primary
							)
							SelectionContainer {
								Text(
									text = "${value ?: stringResource(com.flexify.app.generated.resources.Res.string.info_unknown)}",
									style = MaterialTheme.typography.bodyLarge
								)
							}
						}
					}
				}
			}
		}
	}
}

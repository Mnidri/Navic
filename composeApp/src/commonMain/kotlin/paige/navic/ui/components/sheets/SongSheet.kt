package com.flexify.app.ui.components.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_add_to_another_playlist
import com.flexify.app.composeapp.generated.resources.action_add_to_playlist
import com.flexify.app.composeapp.generated.resources.action_add_to_queue
import com.flexify.app.composeapp.generated.resources.action_cancel_download
import com.flexify.app.composeapp.generated.resources.action_delete_download
import com.flexify.app.composeapp.generated.resources.action_download
import com.flexify.app.composeapp.generated.resources.action_play_next
import com.flexify.app.composeapp.generated.resources.action_remove_from_playlist
import com.flexify.app.composeapp.generated.resources.action_remove_star
import com.flexify.app.composeapp.generated.resources.action_share
import com.flexify.app.composeapp.generated.resources.action_sleep_timer
import com.flexify.app.composeapp.generated.resources.action_sleep_timer_enabled
import com.flexify.app.composeapp.generated.resources.action_star
import com.flexify.app.composeapp.generated.resources.action_track_info
import com.flexify.app.composeapp.generated.resources.action_view_album
import com.flexify.app.composeapp.generated.resources.action_view_artist
import com.flexify.app.composeapp.generated.resources.info_click_to_retry
import com.flexify.app.composeapp.generated.resources.info_download_failed
import com.flexify.app.composeapp.generated.resources.option_playback_speed
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import com.flexify.app.LocalNavStack
import com.flexify.app.LocalPlatformContext
import com.flexify.app.data.database.entities.DownloadStatus
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.manager.SleepTimerManager
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.domain.models.DomainExplicitStatus
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.icons.Icons
import com.flexify.app.icons.filled.Star
import com.flexify.app.icons.outlined.Album
import com.flexify.app.icons.outlined.Artist
import com.flexify.app.icons.outlined.Bedtime
import com.flexify.app.icons.outlined.Close
import com.flexify.app.icons.outlined.Delete
import com.flexify.app.icons.outlined.Download
import com.flexify.app.icons.outlined.DownloadOff
import com.flexify.app.icons.outlined.Info
import com.flexify.app.icons.outlined.PlaylistAdd
import com.flexify.app.icons.outlined.PlaylistRemove
import com.flexify.app.icons.outlined.Queue
import com.flexify.app.icons.outlined.QueuePlayNext
import com.flexify.app.icons.outlined.Share
import com.flexify.app.icons.outlined.Speed
import com.flexify.app.icons.outlined.Star
import com.flexify.app.ui.components.common.CoverArt
import com.flexify.app.ui.components.common.MarqueeText
import com.flexify.app.ui.components.common.RatingRow
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.ui.theme.positive
import com.flexify.app.util.core.InlineExplicitIcon
import com.flexify.app.util.core.label

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SongSheet(
	onDismissRequest: () -> Unit,
	song: DomainSong,
	collection: DomainSongCollection? = null,
	starred: Boolean? = null,
	onSetStarred: ((Boolean) -> Unit)? = null,
	onShare: (() -> Unit)? = null,
	onPlayNext: (() -> Unit)? = null,
	onAddToQueue: (() -> Unit)? = null,
	onTrackInfo: (() -> Unit)? = null,
	onViewAlbum: (() -> Unit)? = null,
	onViewArtist: (() -> Unit)? = null,
	onAddToPlaylist: (() -> Unit)? = null,
	onRemoveFromPlaylist: (() -> Unit)? = null,
	downloadStatus: DownloadStatus? = null,
	onDownload: (() -> Unit)? = null,
	onCancelDownload: (() -> Unit)? = null,
	onDeleteDownload: (() -> Unit)? = null,
	rating: Int? = null,
	onSetRating: ((Int) -> Unit)? = null,
	showSleepTimer: Boolean = false,
	showPlaybackSpeed: Boolean = false
) {
	val preferenceManager = koinInject<PreferenceManager>()

	val platformContext = LocalPlatformContext.current
	val backStack = LocalNavStack.current
	var sleepTimerSheetShown by rememberSaveable { mutableStateOf(false) }
	val sleepTimerManager = koinInject<SleepTimerManager>()
	val sleepTimerLeft = sleepTimerManager.timeLeft
	val contentPadding = PaddingValues(horizontal = 16.dp)
	val colors = ListItemDefaults.colors(
		containerColor = Color.Transparent,
		trailingIconColor = MaterialTheme.colorScheme.onSurface,
		headlineColor = MaterialTheme.colorScheme.onSurface
	)

	ModalBottomSheet(
		onDismissRequest = onDismissRequest,
		dragHandle = null,
		sheetState = rememberModalBottomSheetState(true),
		contentWindowInsets = {
			BottomSheetDefaults.modalWindowInsets.add(
				WindowInsets(
					left = 8.dp,
					right = 8.dp
				)
			)
		}
	) {
		Spacer(Modifier.height(16.dp))

		ListItem(
			headlineContent = {
				MarqueeText(
					text = buildAnnotatedString {
						append(song.title)
						if (song.explicitStatus == DomainExplicitStatus.Explicit) {
							append(" ")
							appendInlineContent("InlineExplicitIcon")
						}
					},
					inlineContent = InlineExplicitIcon,
				)
			},
			supportingContent = {
				MarqueeText(
					"${song.albumTitle ?: ""} • ${song.artistName} • ${song.year ?: ""}"
				)
			},
			leadingContent = {
				CoverArt(
					coverArtId = song.coverArtId,
					modifier = Modifier.size(50.dp),
					shape = preferenceManager.coverArtShape.decreasedShape
				)
			},
			colors = colors
		)
		if (rating != null && onSetRating != null) {
			RatingRow(
				rating = rating,
				setRating = onSetRating
			)
			Spacer(Modifier.height(14.dp))
		}

		HorizontalDivider(Modifier.padding(horizontal = 8.dp, vertical = 2.dp))

		Column(Modifier.verticalScroll(rememberScrollState())) {
			if (onShare != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_share)) },
					leadingContent = { Icon(Icons.Outlined.Share, null) },
					onClick = {
						platformContext.clickSound()
						onShare()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (starred != null && onSetStarred != null) {
				ListItem(
					content = {
						Text(stringResource(if (starred) com.flexify.app.generated.resources.Res.string.action_remove_star else com.flexify.app.generated.resources.Res.string.action_star))
					},
					leadingContent = {
						Icon(if (starred) Icons.Filled.Star else Icons.Outlined.Star, null)
					},
					onClick = {
						platformContext.clickSound()
						onSetStarred(!starred)
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (downloadStatus != null) {
				when (downloadStatus) {
					DownloadStatus.DOWNLOADING -> {
						ListItem(
							content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_cancel_download)) },
							leadingContent = { Icon(Icons.Outlined.Close, null) },
							onClick = {
								platformContext.clickSound()
								onCancelDownload?.invoke()
								onDismissRequest()
							},
							colors = colors,
							contentPadding = contentPadding
						)
					}

					DownloadStatus.DOWNLOADED -> {
						ListItem(
							content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_delete_download)) },
							leadingContent = { Icon(Icons.Outlined.Delete, null) },
							onClick = {
								platformContext.clickSound()
								onDeleteDownload?.invoke()
								onDismissRequest()
							},
							colors = colors,
							contentPadding = contentPadding
						)
					}

					DownloadStatus.FAILED -> {
						ListItem(
							content = {
								Text(
									text = stringResource(com.flexify.app.generated.resources.Res.string.info_download_failed),
									color = MaterialTheme.colorScheme.error
								)
							},
							supportingContent = {
								Text(
									text = stringResource(com.flexify.app.generated.resources.Res.string.info_click_to_retry),
									color = MaterialTheme.colorScheme.error,
									style = MaterialTheme.typography.labelSmall
								)
							},
							leadingContent = {
								Icon(
									Icons.Outlined.DownloadOff,
									null,
									tint = MaterialTheme.colorScheme.error
								)
							},
							onClick = {
								platformContext.clickSound()
								onDownload?.invoke()
								onDismissRequest()
							},
							colors = colors,
							contentPadding = contentPadding
						)
					}

					else -> {
						ListItem(
							content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_download)) },
							leadingContent = { Icon(Icons.Outlined.Download, null) },
							onClick = {
								platformContext.clickSound()
								onDownload?.invoke()
								onDismissRequest()
							},
							colors = colors,
							contentPadding = contentPadding
						)
					}
				}
			} else if (onDownload != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_download)) },
					leadingContent = { Icon(Icons.Outlined.Download, null) },
					onClick = {
						platformContext.clickSound()
						onDownload()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onPlayNext != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_play_next)) },
					leadingContent = { Icon(Icons.Outlined.QueuePlayNext, null) },
					onClick = {
						platformContext.clickSound()
						onPlayNext()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onAddToQueue != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_add_to_queue)) },
					leadingContent = { Icon(Icons.Outlined.Queue, null) },
					onClick = {
						platformContext.clickSound()
						onAddToQueue()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onAddToPlaylist != null) {
				ListItem(
					content = {
						Text(
							stringResource(
								if (collection != null && collection !is DomainAlbum)
									com.flexify.app.generated.resources.Res.string.action_add_to_another_playlist
								else com.flexify.app.generated.resources.Res.string.action_add_to_playlist
							)
						)
					},
					leadingContent = { Icon(Icons.Outlined.PlaylistAdd, null) },
					onClick = {
						platformContext.clickSound()
						onAddToPlaylist()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onRemoveFromPlaylist != null && collection != null && collection !is DomainAlbum) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_remove_from_playlist)) },
					leadingContent = { Icon(Icons.Outlined.PlaylistRemove, null) },
					onClick = {
						platformContext.clickSound()
						onRemoveFromPlaylist()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onViewAlbum != null) {
				ListItem(
					content = {
						Text(stringResource(com.flexify.app.generated.resources.Res.string.action_view_album))
					},
					leadingContent = { Icon(Icons.Outlined.Album, null) },
					onClick = {
						platformContext.clickSound()
						onViewAlbum()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onViewArtist != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_view_artist)) },
					leadingContent = { Icon(Icons.Outlined.Artist, null) },
					onClick = {
						platformContext.clickSound()
						onViewArtist()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (showSleepTimer) {
				if (sleepTimerLeft != null) {
					ListItem(
						content = {
							Text(
								stringResource(
									com.flexify.app.generated.resources.Res.string.action_sleep_timer_enabled,
									sleepTimerLeft.label()
								),
								color = MaterialTheme.colorScheme.positive
							)
						},
						leadingContent = {
							Icon(
								Icons.Outlined.Bedtime,
								null,
								tint = MaterialTheme.colorScheme.positive
							)
						},
						onClick = {
							platformContext.clickSound()
							sleepTimerSheetShown = true
						},
						colors = colors,
						contentPadding = contentPadding
					)
				} else {
					ListItem(
						content = {
							Text(
								stringResource(com.flexify.app.generated.resources.Res.string.action_sleep_timer)
							)
						},
						leadingContent = {
							Icon(
								Icons.Outlined.Bedtime,
								null
							)
						},
						onClick = {
							platformContext.clickSound()
							sleepTimerSheetShown = true
						},
						colors = colors,
						contentPadding = contentPadding
					)
				}
			}

			if (showPlaybackSpeed) {
				ListItem(
					content = {
						Text(
							stringResource(com.flexify.app.generated.resources.Res.string.option_playback_speed)
						)
					},
					leadingContent = {
						Icon(
							Icons.Outlined.Speed,
							null
						)
					},
					onClick = dropUnlessResumed {
						platformContext.clickSound()
						backStack.add(Screen.PlaybackSpeed)
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}

			if (onTrackInfo != null) {
				ListItem(
					content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_track_info)) },
					leadingContent = { Icon(Icons.Outlined.Info, null) },
					onClick = {
						platformContext.clickSound()
						onTrackInfo()
						onDismissRequest()
					},
					colors = colors,
					contentPadding = contentPadding
				)
			}
		}
	}

	if (sleepTimerSheetShown) {
		SleepTimerSheet(
			onDismissRequest = { confirmed ->
				sleepTimerSheetShown = false
				if (confirmed) {
					onDismissRequest()
				}
			}
		)
	}
}

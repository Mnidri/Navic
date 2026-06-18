package com.flexify.app.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.SingletonImageLoader
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_cancel_download
import com.flexify.app.composeapp.generated.resources.action_clear_downloads
import com.flexify.app.composeapp.generated.resources.action_clear_image_cache
import com.flexify.app.composeapp.generated.resources.action_clear_pending_actions
import com.flexify.app.composeapp.generated.resources.action_rebuild_database
import com.flexify.app.composeapp.generated.resources.action_trigger_sync
import com.flexify.app.composeapp.generated.resources.count_songs
import com.flexify.app.composeapp.generated.resources.info_library_download
import com.flexify.app.composeapp.generated.resources.info_library_download_warning
import com.flexify.app.composeapp.generated.resources.info_not_available_offline
import com.flexify.app.composeapp.generated.resources.info_progress
import com.flexify.app.composeapp.generated.resources.info_status_calculating
import com.flexify.app.composeapp.generated.resources.info_status_downloading
import com.flexify.app.composeapp.generated.resources.info_sync_date_format
import com.flexify.app.composeapp.generated.resources.info_sync_hours_ago
import com.flexify.app.composeapp.generated.resources.info_sync_just_now
import com.flexify.app.composeapp.generated.resources.info_sync_mins_ago
import com.flexify.app.composeapp.generated.resources.info_sync_never
import com.flexify.app.composeapp.generated.resources.option_cover_art_quality
import com.flexify.app.composeapp.generated.resources.option_downloaded_songs
import com.flexify.app.composeapp.generated.resources.option_image_cache_size
import com.flexify.app.composeapp.generated.resources.option_last_sync
import com.flexify.app.composeapp.generated.resources.option_live_status
import com.flexify.app.composeapp.generated.resources.option_offline_mode
import com.flexify.app.composeapp.generated.resources.option_pending_actions
import com.flexify.app.composeapp.generated.resources.subtitle_offline_mode
import com.flexify.app.composeapp.generated.resources.subtitle_pending_actions
import com.flexify.app.composeapp.generated.resources.subtitle_rebuild_database
import com.flexify.app.composeapp.generated.resources.subtitle_trigger_sync
import com.flexify.app.composeapp.generated.resources.title_cache_management
import com.flexify.app.composeapp.generated.resources.title_danger_zone
import com.flexify.app.composeapp.generated.resources.title_data_storage
import com.flexify.app.composeapp.generated.resources.title_library_download
import com.flexify.app.composeapp.generated.resources.title_network
import com.flexify.app.composeapp.generated.resources.title_sync_control
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.settings.CoverArtQuality
import com.flexify.app.domain.models.settings.OfflineMode
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Offline
import com.flexify.app.ui.components.common.Form
import com.flexify.app.ui.components.common.FormRow
import com.flexify.app.ui.components.common.FormTitle
import com.flexify.app.ui.components.dialogs.BulkDownloadDialog
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.screens.settings.components.SettingSelectionRow
import com.flexify.app.ui.screens.settings.viewmodels.SettingsDataStorageViewModel
import kotlin.time.Clock
import kotlin.time.Instant
import coil3.compose.LocalPlatformContext as LocalCoilPlatformContext

@Composable
fun SettingsDataStorageScreen() {
	val viewModel = koinViewModel<SettingsDataStorageViewModel>()

	val platformContext = LocalPlatformContext.current
	val preferenceManager = koinInject<PreferenceManager>()
	val scope = rememberCoroutineScope()
	val coilPlatformContext = LocalCoilPlatformContext.current
	val imageLoader = SingletonImageLoader.get(coilPlatformContext)

	val syncState by viewModel.syncState.collectAsStateWithLifecycle()
	val pendingActionCount by viewModel.pendingActionCount.collectAsStateWithLifecycle()
	val downloadCount by viewModel.downloadCount.collectAsStateWithLifecycle(0)
	val downloadSize by viewModel.downloadSize.collectAsStateWithLifecycle(0L)

	var showLibraryDownloadDialog by remember { mutableStateOf(false) }
	val isDownloadingLibrary by viewModel.isDownloadingLibrary.collectAsStateWithLifecycle()
	val libraryDownloadProgress by viewModel.libraryDownloadProgress.collectAsStateWithLifecycle()

	val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

	val calculating = stringResource(com.flexify.app.generated.resources.Res.string.info_status_calculating)
	var imageCacheSizeMb by remember { mutableStateOf(calculating) }

	val downloadsSizeMb = remember(downloadSize) {
		val mb = downloadSize.toDouble() / (1024 * 1024)
		if (mb > 1024) {
			val gb = mb / 1024
			" // ${(gb * 100).toInt() / 100.0} GB"
		} else {
			" // ${mb.toInt()} MB"
		}
	}

	val smoothSyncProgress by animateFloatAsState(
		if (syncState.isSyncing) syncState.progress else 0f,
		animationSpec = tween(
			durationMillis = 250,
			easing = EaseOut
		)
	)

	val smoothLibraryDownloadProgress by animateFloatAsState(
		targetValue = libraryDownloadProgress.coerceIn(0f, 1f),
		animationSpec = tween(durationMillis = 500, easing = EaseOut)
	)

	val offlineModifier = Modifier.alpha(if (isOnline) 1f else 0.75f)
	val offlineIcon = @Composable {
		if (!isOnline) {
			Icon(
				Icons.Outlined.Offline,
				stringResource(com.flexify.app.generated.resources.Res.string.info_not_available_offline),
				modifier = Modifier.size(20.dp)
			)
		}
	}

	LaunchedEffect(Unit) {
		withContext(Dispatchers.IO) {
			val sizeBytes = imageLoader.diskCache?.size ?: 0L
			imageCacheSizeMb = "${sizeBytes / (1024 * 1024)} MB"
		}
	}

	BulkDownloadDialog(
		title = stringResource(com.flexify.app.generated.resources.Res.string.title_library_download),
		message = stringResource(com.flexify.app.generated.resources.Res.string.info_library_download_warning),
		showDialog = showLibraryDownloadDialog,
		onDismissRequest = { showLibraryDownloadDialog = false },
		onConfirm = {
			showLibraryDownloadDialog = false
			viewModel.downloadEntireLibrary()
		}
	)

	Scaffold(
		topBar = {
			NestedTopBar(
				title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.title_data_storage)) },
				hideBack = platformContext.sizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
			)
		},
		contentWindowInsets = WindowInsets.statusBars
	) { innerPadding ->
		CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
			Column(
				Modifier
					.padding(innerPadding)
					.verticalScroll(rememberScrollState())
					.padding(top = 16.dp, end = 16.dp, start = 16.dp, bottom = 32.dp)
			) {
				FormTitle(stringResource(com.flexify.app.generated.resources.Res.string.title_network))
				Form {
					SettingSelectionRow(
						title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.option_offline_mode)) },
						items = OfflineMode.entries.toImmutableList(),
						label = { stringResource(it.displayName) },
						description = stringResource(com.flexify.app.generated.resources.Res.string.subtitle_offline_mode),
						selection = preferenceManager.offlineMode,
						onSelect = { preferenceManager.offlineMode = it }
					)
					SettingSelectionRow(
						title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.option_cover_art_quality)) },
						items = CoverArtQuality.entries.toImmutableList(),
						label = { stringResource(it.displayName) },
						selection = preferenceManager.coverArtQuality,
						onSelect = {
							preferenceManager.coverArtQuality = it
							imageLoader.memoryCache?.clear()
							scope.launch(Dispatchers.IO) {
								imageLoader.diskCache?.clear()
								imageCacheSizeMb = "0 MB"
							}
						}
					)
				}

				FormTitle(stringResource(com.flexify.app.generated.resources.Res.string.title_sync_control))
				Form {
					FormRow {
						Column(Modifier.fillMaxWidth()) {
							Column {
								Text(stringResource(com.flexify.app.generated.resources.Res.string.option_live_status))
								Text(
									text = stringResource(syncState.message),
									style = MaterialTheme.typography.bodyMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
							AnimatedVisibility(
								syncState.isSyncing,
								enter = fadeIn() + expandVertically(clip = false),
								exit = fadeOut() + shrinkVertically(clip = false)
							) {
								LinearProgressIndicator(
									progress = {
										if (!syncState.isSyncing)
											1f
										else smoothSyncProgress.coerceIn(0f, 1f)
									},
									modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
								)
							}
						}
					}

					FormRow(
						modifier = offlineModifier,
						onClick = if (isOnline) {
							{ viewModel.triggerManualSync() }
						} else null
					) {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.action_trigger_sync))
							Text(
								stringResource(com.flexify.app.generated.resources.Res.string.subtitle_trigger_sync),
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
						offlineIcon()
					}

					FormRow {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.option_last_sync))
							Text(
								text = if (preferenceManager.lastFullSyncTime == 0L) {
									stringResource(com.flexify.app.generated.resources.Res.string.info_sync_never)
								} else {
									Instant.fromEpochMilliseconds(
										preferenceManager.lastFullSyncTime
									).toRelativeString(
										justNow = stringResource(com.flexify.app.generated.resources.Res.string.info_sync_just_now),
										minsAgo = stringResource(com.flexify.app.generated.resources.Res.string.info_sync_mins_ago),
										hoursAgo = stringResource(com.flexify.app.generated.resources.Res.string.info_sync_hours_ago),
										dateFormat = stringResource(com.flexify.app.generated.resources.Res.string.info_sync_date_format)
									)
								},
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				}

				FormTitle(stringResource(com.flexify.app.generated.resources.Res.string.title_cache_management))
				Form {
					FormRow {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.option_pending_actions))
							Text(
								stringResource(
									com.flexify.app.generated.resources.Res.string.subtitle_pending_actions,
									pendingActionCount
								),
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}

					FormRow {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.option_downloaded_songs))
							Text(
								pluralStringResource(
									com.flexify.app.generated.resources.Res.plurals.count_songs,
									downloadCount,
									downloadCount
								)
									+ downloadsSizeMb,
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}

					FormRow {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.option_image_cache_size))
							Text(
								imageCacheSizeMb,
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}

					FormRow(
						modifier = offlineModifier,
						onClick = if (!isDownloadingLibrary && isOnline) {
							{ showLibraryDownloadDialog = true }
						} else null
					) {
						Column(Modifier.weight(1f)) {
							Text(stringResource(com.flexify.app.generated.resources.Res.string.title_library_download))
							Text(
								text = stringResource(if (isDownloadingLibrary) com.flexify.app.generated.resources.Res.string.info_status_downloading else com.flexify.app.generated.resources.Res.string.info_library_download),
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)

							AnimatedVisibility(
								visible = isDownloadingLibrary,
								enter = fadeIn() + expandVertically(clip = false),
								exit = fadeOut() + shrinkVertically(clip = false)
							) {
								Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
									Row(
										modifier = Modifier.fillMaxWidth(),
										horizontalArrangement = Arrangement.SpaceBetween,
										verticalAlignment = Alignment.CenterVertically
									) {
										Text(
											text = stringResource(com.flexify.app.generated.resources.Res.string.info_progress),
											style = MaterialTheme.typography.labelMedium,
											color = MaterialTheme.colorScheme.primary
										)

										Row(verticalAlignment = Alignment.CenterVertically) {
											TextButton(
												onClick = {
													platformContext.clickSound()
													viewModel.cancelLibraryDownload()
												},
												contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
												modifier = Modifier.padding(end = 8.dp)
											) {
												Text(
													stringResource(com.flexify.app.generated.resources.Res.string.action_cancel_download),
													style = MaterialTheme.typography.labelLarge,
													color = MaterialTheme.colorScheme.error
												)
											}

											Text(
												text = "${(smoothLibraryDownloadProgress * 100).toInt()}%",
												style = MaterialTheme.typography.labelMedium,
												color = MaterialTheme.colorScheme.primary
											)
										}
									}

									LinearProgressIndicator(
										progress = { smoothLibraryDownloadProgress },
										modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
									)
								}
							}
						}
						offlineIcon()
					}
				}

				FormTitle(stringResource(com.flexify.app.generated.resources.Res.string.title_danger_zone))
				Form {
					FormRow(
						onClick = {
							imageLoader.memoryCache?.clear()
							scope.launch(Dispatchers.IO) {
								imageLoader.diskCache?.clear()
								imageCacheSizeMb = "0 MB"
							}
						}
					) {
						Text(
							stringResource(com.flexify.app.generated.resources.Res.string.action_clear_image_cache),
							color = MaterialTheme.colorScheme.error,
							modifier = Modifier.weight(1f)
						)
					}

					FormRow(onClick = { viewModel.removeAllActions() }) {
						Text(
							stringResource(com.flexify.app.generated.resources.Res.string.action_clear_pending_actions),
							color = MaterialTheme.colorScheme.error
						)
					}

					FormRow(onClick = { viewModel.clearAllDownloads() }) {
						Text(
							stringResource(com.flexify.app.generated.resources.Res.string.action_clear_downloads),
							color = MaterialTheme.colorScheme.error
						)
					}

					FormRow(
						modifier = offlineModifier,
						onClick = if (isOnline) {
							{ viewModel.rebuildDatabase() }
						} else null
					) {
						Column(Modifier.weight(1f)) {
							Text(
								stringResource(com.flexify.app.generated.resources.Res.string.action_rebuild_database),
								color = MaterialTheme.colorScheme.error
							)
							Text(
								stringResource(com.flexify.app.generated.resources.Res.string.subtitle_rebuild_database),
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
							)
						}
						offlineIcon()
					}
				}
			}
		}
	}
}

private fun Instant.toRelativeString(
	justNow: String,
	minsAgo: String,
	hoursAgo: String,
	dateFormat: String
): String {
	val now = Clock.System.now()
	val diff = now - this
	val seconds = diff.inWholeSeconds

	return when {
		seconds < 60 -> justNow
		seconds < 3600 -> minsAgo.replace($$"%1$d", (seconds / 60).toString())
		seconds < 86400 -> hoursAgo.replace($$"%1$d", (seconds / 3600).toString())
		else -> {
			val date = this.toLocalDateTime(TimeZone.currentSystemDefault())
			val monthName = date.month.name.lowercase().take(3)
			dateFormat
				.replace($$"%1$d", date.day.toString())
				.replace($$"%1$s", monthName)
		}
	}
}

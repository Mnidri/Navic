package com.flexify.app

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.Companion.detailPane
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.Companion.listPane
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.NavDisplay.popTransitionSpec
import androidx.navigation3.ui.NavDisplay.predictivePopTransitionSpec
import androidx.navigation3.ui.NavDisplay.transitionSpec
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.compose.setSingletonImageLoaderFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import com.flexify.app.di.initializeSingletonImageLoader
import com.flexify.app.domain.manager.BottomBarScrollManager
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.manager.SessionManager
import com.flexify.app.shared.MediaPlayerViewModel
import com.flexify.app.ui.components.snackbars.NavicSnackbar
import com.flexify.app.ui.components.dialogs.SideloadingDialog
import com.flexify.app.ui.components.sheets.ChangelogSheet
import com.flexify.app.domain.manager.SnackBarManager
import com.flexify.app.ui.navigation.BottomSheetSceneStrategy
import com.flexify.app.ui.navigation.NowPlayingSceneStrategy
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.ui.screens.album.AlbumListScreen
import com.flexify.app.ui.screens.artist.ArtistDetailScreen
import com.flexify.app.ui.screens.artist.ArtistListScreen
import com.flexify.app.ui.screens.collection.CollectionDetailScreen
import com.flexify.app.ui.screens.genre.GenreListScreen
import com.flexify.app.ui.screens.library.LibraryScreen
import com.flexify.app.ui.screens.login.LoginScreen
import com.flexify.app.ui.screens.lyrics.LyricsScreen
import com.flexify.app.ui.screens.nowPlaying.NowPlayingScreen
import com.flexify.app.ui.screens.nowPlaying.PlaybackSpeedScreen
import com.flexify.app.ui.screens.playlist.PlaylistListScreen
import com.flexify.app.ui.screens.queue.QueueScreen
import com.flexify.app.ui.screens.radio.RadioListScreen
import com.flexify.app.ui.screens.search.SearchScreen
import com.flexify.app.ui.screens.settings.BottomBarScreen
import com.flexify.app.ui.screens.settings.FontsScreen
import com.flexify.app.ui.screens.settings.SettingsAboutScreen
import com.flexify.app.ui.screens.settings.SettingsAcknowledgementsScreen
import com.flexify.app.ui.screens.settings.SettingsAppearanceScreen
import com.flexify.app.ui.screens.settings.SettingsCustomHeadersScreen
import com.flexify.app.ui.screens.settings.SettingsDataStorageScreen
import com.flexify.app.ui.screens.settings.SettingsDeveloperScreen
import com.flexify.app.ui.screens.settings.SettingsLogsScreen
import com.flexify.app.ui.screens.settings.SettingsNowPlayingScreen
import com.flexify.app.ui.screens.settings.SettingsPlaybackScreen
import com.flexify.app.ui.screens.settings.SettingsScreen
import com.flexify.app.ui.screens.settings.SettingsStreamingQualityScreen
import com.flexify.app.ui.screens.share.ShareListScreen
import com.flexify.app.ui.screens.song.SongDetailScreen
import com.flexify.app.ui.screens.song.SongListScreen
import com.flexify.app.ui.screens.starred.StarredScreen
import com.flexify.app.ui.theme.NavicTheme
import com.flexify.app.util.core.PlatformContext
import com.flexify.app.util.core.PlatformType
import com.flexify.app.util.core.rememberPlatformContext
import com.flexify.app.util.ui.Material3Transitions

@OptIn(ExperimentalSerializationApi::class)
private val config = SavedStateConfiguration {
	serializersModule = SerializersModule {
		polymorphic(NavKey::class) {
			subclassesOfSealed<Screen>()
		}
	}
}

val LocalPlatformContext = staticCompositionLocalOf<PlatformContext> { error("no platform context") }
val LocalNavStack = staticCompositionLocalOf<NavBackStack<NavKey>> { error("no backstack") }
val LocalSnackbarState = staticCompositionLocalOf<SnackbarHostState> { error("no snackbar state") }
val LocalSharedTransitionScope =
	staticCompositionLocalOf<SharedTransitionScope> { error("no shared transition scope") }

val LocalBottomBarScrollManager = staticCompositionLocalOf<BottomBarScrollManager> {
	error("No BottomBarScrollManager provided")
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
	// TODO: wtf was this for
	setSingletonImageLoaderFactory { platformContext ->
		initializeSingletonImageLoader(platformContext)
	}

	val platformContext = rememberPlatformContext()
	val sessionManager = koinInject<SessionManager>()
	val preferenceManager = koinInject<PreferenceManager>()
	val isLoggedIn by sessionManager.isLoggedIn.collectAsStateWithLifecycle()
	val backStack = rememberNavBackStack(
		config, if (isLoggedIn) {
			Screen.Library()
		} else {
			Screen.Login
		}
	)
	val snackbarState = remember { SnackbarHostState() }
	val snackBarManager = koinInject<SnackBarManager>()

	LaunchedEffect(Unit) {
		snackBarManager.events.collectLatest { event ->
			snackbarState.showSnackbar(getString(event.resource, *event.args.toTypedArray()))
		}
	}

	val density = LocalDensity.current
	val layoutDirection = LocalLayoutDirection.current
	val scrollManager = remember {
		BottomBarScrollManager(with(density) { 50.dp.toPx() })
	}

	SharedTransitionLayout {
		CompositionLocalProvider(
			LocalPlatformContext provides platformContext,
			LocalNavStack provides backStack,
			LocalSnackbarState provides snackbarState,
			LocalSharedTransitionScope provides this@SharedTransitionLayout,
			LocalBottomBarScrollManager provides scrollManager
		) {
			NavicTheme {
				Scaffold(
					modifier = Modifier.nestedScroll(scrollManager.connection),
					snackbarHost = {
						SnackbarHost(hostState = snackbarState) { snackbarData ->
							NavicSnackbar(snackbarData = snackbarData)
						}
					}
				) { contentPadding ->
					NavDisplay(
						modifier = Modifier
							.padding(
								start = contentPadding
									.calculateStartPadding(layoutDirection),
								end = contentPadding
									.calculateEndPadding(layoutDirection)
							)
							.fillMaxSize()
							.background(MaterialTheme.colorScheme.surface),
						backStack = backStack,
						sceneStrategies = listOf(
							remember { NowPlayingSceneStrategy() },
							remember { BottomSheetSceneStrategy() },
							rememberListDetailSceneStrategy()
						),
						onBack = {
							if (backStack.isNotEmpty()) {
								backStack.removeLastOrNull()
							}
						},
						entryProvider = entryProvider(backStack),
						transitionSpec = {
							Material3Transitions.SharedXAxisEnterTransition(
								density
							) togetherWith Material3Transitions.SharedXAxisExitTransition(
								density
							)
						},
						popTransitionSpec = {
							Material3Transitions.SharedXAxisPopEnterTransition(
								density
							) togetherWith Material3Transitions.SharedXAxisPopExitTransition(
								density
							)
						},
						predictivePopTransitionSpec = {
							slideInHorizontally(
								animationSpec = tween(300, easing = EaseOutQuart),
								initialOffsetX = { -it }
							) togetherWith slideOutHorizontally(
								animationSpec = tween(300, easing = EaseOutQuart),
								targetOffsetX = { it }
							)
						}
					)
				}
				if (!preferenceManager.showedSideloadingWarning
					&& platformContext.name.lowercase().contains("android")
				) {
					SideloadingDialog()
				}
				// version check is annoying to do on iOS
				if (preferenceManager.checkForUpdates && platformContext.platformType == PlatformType.Android) {
					ChangelogSheet()
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
private fun entryProvider(
	backStack: NavBackStack<NavKey>
): (NavKey) -> (NavEntry<NavKey>) {
	val navtabMetadata = if (backStack.size == 1)
		listPane("root") + transitionSpec {
			ContentTransform(fadeIn(), fadeOut())
		} + popTransitionSpec {
			ContentTransform(fadeIn(), fadeOut())
		} + predictivePopTransitionSpec {
			ContentTransform(fadeIn(), fadeOut())
		}
	else listPane("root")
	return androidx.navigation3.runtime.entryProvider {
		// tabs
		entry<Screen.Library>(metadata = navtabMetadata) {
			LibraryScreen()
		}
		entry<Screen.Starred>(metadata = navtabMetadata) {
			StarredScreen()
		}
		entry<Screen.AlbumList>(metadata = navtabMetadata) { key ->
			AlbumListScreen(key.nested, key.listType)
		}
		entry<Screen.PlaylistList>(metadata = navtabMetadata) { key ->
			PlaylistListScreen(key.nested)
		}
		entry<Screen.ArtistList>(metadata = navtabMetadata) { key ->
			ArtistListScreen(key.nested, key.listType)
		}
		entry<Screen.GenreList>(metadata = navtabMetadata) { key ->
			GenreListScreen(key.nested)
		}
		entry<Screen.SongList>(metadata = navtabMetadata) { key ->
			SongListScreen(key.nested, key.artistId, key.artistName, key.listType)
		}

		entry<Screen.RadioList>(metadata = navtabMetadata) { key ->
			RadioListScreen(key.nested)
		}

		// misc
		entry<Screen.Login> {
			LoginScreen()
		}
		entry<Screen.NowPlaying>(
			metadata = NowPlayingSceneStrategy.bottomSheet(maxWidth = Dp.Unspecified)
		) {
			NowPlayingScreen()
		}
		entry<Screen.Lyrics>(metadata = NowPlayingSceneStrategy.bottomSheet(isTransparent = true)) {
			val player = koinInject<MediaPlayerViewModel>()
			val playerState by player.uiState.collectAsState()
			val song = playerState.currentSong
			LyricsScreen(song)
		}
		entry<Screen.Queue>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
			QueueScreen()
		}
		entry<Screen.PlaybackSpeed>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
			PlaybackSpeedScreen()
		}
		entry<Screen.CollectionDetail>(metadata = detailPane("root")) { key ->
			CollectionDetailScreen(key.collectionId, key.tab)
		}
		entry<Screen.SongDetail>(metadata = detailPane("root")) { key ->
			SongDetailScreen(key.songId)
		}
		entry<Screen.Search>(metadata = navtabMetadata) { key ->
			SearchScreen(key.nested)
		}
		entry<Screen.ShareList> {
			ShareListScreen()
		}
		entry<Screen.ArtistDetail> { key ->
			ArtistDetailScreen(key.artist)
		}

		// settings
		entry<Screen.Settings.Root>(metadata = listPane("settings")) {
			SettingsScreen()
		}
		entry<Screen.Settings.Appearance>(metadata = detailPane("settings")) {
			SettingsAppearanceScreen()
		}
		entry<Screen.Settings.BottomAppBar>(metadata = detailPane("settings")) {
			BottomBarScreen()
		}
		entry<Screen.Settings.NowPlaying>(metadata = detailPane("settings")) {
			SettingsNowPlayingScreen()
		}
		entry<Screen.Settings.Playback>(metadata = detailPane("settings")) {
			SettingsPlaybackScreen()
		}
		entry<Screen.Settings.Developer>(metadata = detailPane("settings")) {
			SettingsDeveloperScreen()
		}
		entry<Screen.Settings.About>(metadata = detailPane("settings")) {
			SettingsAboutScreen()
		}
		entry<Screen.Settings.Acknowledgements>(metadata = detailPane("settings")) {
			SettingsAcknowledgementsScreen()
		}
		entry<Screen.Settings.DataStorage>(metadata = detailPane("settings")) {
			SettingsDataStorageScreen()
		}
		entry<Screen.Settings.Fonts> {
			FontsScreen()
		}
		entry<Screen.Settings.CustomHeaders> {
			SettingsCustomHeadersScreen()
		}
		entry<Screen.Settings.StreamingQuality> {
			SettingsStreamingQualityScreen()
		}
		entry<Screen.Settings.Logs> {
			SettingsLogsScreen()
		}
	}
}

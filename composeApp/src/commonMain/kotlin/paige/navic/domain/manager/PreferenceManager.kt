package com.flexify.app.domain.manager

import com.flexify.app.domain.manager.base.BasePreferenceManager
import com.flexify.app.domain.models.settings.AnimationStyle
import com.flexify.app.domain.models.settings.BottomBarCollapseMode
import com.flexify.app.domain.models.settings.BottomBarVisibilityMode
import com.flexify.app.domain.models.settings.CoverArtQuality
import com.flexify.app.domain.models.settings.CoverArtShape
import com.flexify.app.domain.models.settings.FontOption
import com.flexify.app.domain.models.settings.GridSize
import com.flexify.app.domain.models.settings.MarqueeSpeed
import com.flexify.app.domain.models.settings.MiniPlayerProgressStyle
import com.flexify.app.domain.models.settings.MiniPlayerStyle
import com.flexify.app.domain.models.settings.NavigationBarLabelVisibility
import com.flexify.app.domain.models.settings.NavigationBarStyle
import com.flexify.app.domain.models.settings.NowPlayingBackgroundStyle
import com.flexify.app.domain.models.settings.NowPlayingSliderStyle
import com.flexify.app.domain.models.settings.OfflineMode
import com.flexify.app.domain.models.settings.ReplayGainMode
import com.flexify.app.domain.models.settings.StreamingQuality
import com.flexify.app.domain.models.settings.Theme
import com.flexify.app.domain.models.settings.ThemeMode
import com.flexify.app.domain.models.settings.ToolbarPosition
import com.russhwolf.settings.Settings as KmpSettings

class PreferenceManager(
	settings: KmpSettings
) : BasePreferenceManager(settings) {
	var font by preference(FontOption.GoogleSans)
	var fontPath by preference("")
	var animationStyle by preference(AnimationStyle.Expressive)
	var nowPlayingBackgroundStyle by preference(NowPlayingBackgroundStyle.Dynamic)
	var swipeToSkip by preference(true)
	var gridSize by preference(GridSize.TwoByTwo)
	var coverArtShape by preference(CoverArtShape.Soft)
	var coverArtQuality by preference(CoverArtQuality.High)
	var artGridItemSize by preference(150f)
	var marqueeSpeed by preference(MarqueeSpeed.Slow)
	var alphabeticalScroll by preference(false)
	var lyricsAutoscroll by preference(true)
	var lyricsBeatByBeat by preference(true)
	var lyricsKeepAlive by preference(true)
	var lyricsBlur by preference(false)
	var lyricsBrightInactive by preference(false)
	var enableScrobbling by preference(true)
	var scrobblePercentage by preference(.5f)
	var minDurationToScrobble by preference(30f)
	var replayGainMode by preference(ReplayGainMode.Off)
	var gaplessPlayback by preference(true)
	var audioOffload by preference(false)
	var streamingQualityWifi by preference(StreamingQuality.Lossless)
	var streamingQualityCellular by preference(StreamingQuality.Lossless)
	var isAdvancedTranscodingActive by preference(false)
	var customMaxBitrateWifi by preference(0)
	var customMaxBitrateCellular by preference(0)
	var nowPlayingToolbarPosition by preference(ToolbarPosition.Bottom)
	var nowPlayingSongInfo by preference(true)
	var nowPlayingSliderStyle by preference(NowPlayingSliderStyle.Squiggly)
	var customHeaders by preference("")
	var checkForUpdates by preference(true)

	// navigation bar settings
	var bottomBarCollapseMode by preference(BottomBarCollapseMode.OnScroll)
	var bottomBarVisibilityMode by preference(BottomBarVisibilityMode.AllScreens)
	var navigationBarStyle by preference(NavigationBarStyle.Normal)
	var navigationBarLabelVisibility by preference(
        NavigationBarLabelVisibility.Always
    )
	var miniPlayerStyle by preference(MiniPlayerStyle.Detached)
	var miniPlayerProgressStyle by preference(MiniPlayerProgressStyle.Seekable)

	/**
	 * If we have informed the user (on Android) about
	 * Google locking down sideloading.
	 */
	var showedSideloadingWarning by preference(false)

	// theme related settings
	var theme by preference(Theme.Dynamic)
	var themeMode by preference(ThemeMode.System)
	var accentColourH by preference(0f)
	var accentColourS by preference(0f)
	var accentColourV by preference(1f)

	// sync related settings
	var lastFullSyncTime by preference(0L)

	fun customHeadersMap(): Map<String, String> = buildMap {
		for (line in customHeaders.lines()) {
			val parts = line.split(":", limit = 2)
			if (parts.size < 2) continue

			val rawKey = parts[0]
			val rawValue = parts[1]

			val key = rawKey.trim()
			val value = rawValue.trim()
			if (key.isNotEmpty() && value.isNotEmpty()) put(key, value)
		}
	}

	var offlineMode by preference(OfflineMode.Auto)
}

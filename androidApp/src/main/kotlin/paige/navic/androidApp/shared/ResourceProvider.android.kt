package com.flexify.app.androidApp.shared

import com.flexify.app.util.core.ResourceProvider

class AndroidResourceProvider(
	override val icNavic: Int = com.flexify.app.androidApp.R.drawable.ic_navic,
	override val animLibrary: Int = com.flexify.app.androidApp.R.drawable.anim_library,
	override val animPlaylist: Int = com.flexify.app.androidApp.R.drawable.anim_playlist,
	override val animArtist: Int = com.flexify.app.androidApp.R.drawable.anim_artist,
	override val animPause: Int = com.flexify.app.androidApp.R.drawable.anim_pause
) : ResourceProvider

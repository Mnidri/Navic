package com.flexify.app.domain.models.lyrics

import kotlin.time.Duration

data class LyricsLine(
	val time: Duration? = null,
	val text: String,
	val words: List<LyricsWord>? = null
)

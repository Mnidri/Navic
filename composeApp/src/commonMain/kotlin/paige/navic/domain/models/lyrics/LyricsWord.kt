package com.flexify.app.domain.models.lyrics

import kotlin.time.Duration

data class LyricsWord(
	val time: Duration,
	val duration: Duration,
	val text: String
)

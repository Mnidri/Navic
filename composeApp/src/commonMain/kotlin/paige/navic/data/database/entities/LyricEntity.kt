package com.flexify.app.data.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.flexify.app.domain.models.lyrics.LyricsProvider

@Entity
data class LyricEntity(
	@PrimaryKey val songId: String,
	val rawContent: String,
	val provider: LyricsProvider
)

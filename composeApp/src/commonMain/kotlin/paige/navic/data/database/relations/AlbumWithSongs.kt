package com.flexify.app.data.database.relations

import androidx.room3.Embedded
import androidx.room3.Relation
import com.flexify.app.data.database.entities.AlbumEntity
import com.flexify.app.data.database.entities.SongEntity

data class AlbumWithSongs(
	@Embedded val album: AlbumEntity,
	@Relation(
		parentColumns = ["albumId"],
		entityColumns = ["belongsToAlbumId"]
	)
	val songs: List<SongEntity>
)

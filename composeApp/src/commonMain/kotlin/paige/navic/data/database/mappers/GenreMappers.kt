package com.flexify.app.data.database.mappers

import com.flexify.app.data.database.entities.GenreEntity
import com.flexify.app.data.database.relations.GenreWithAlbums
import com.flexify.app.domain.models.DomainGenre
import dev.zt64.subsonic.api.model.Genre as ApiGenre

fun ApiGenre.toEntity() = GenreEntity(
	genreName = name,
	albumCount = albumCount,
	songCount = songCount
)

fun GenreWithAlbums.toDomainModel() = DomainGenre(
	name = genre.genreName,
	albumCount = genre.albumCount,
	songCount = genre.songCount,
	albums = albums.map { it.toDomainModel() }
)

fun DomainGenre.toEntity() = GenreEntity(
	genreName = name,
	albumCount = albumCount,
	songCount = songCount
)

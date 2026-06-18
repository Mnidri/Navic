package com.flexify.app.data.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters
import com.flexify.app.data.database.dao.AlbumDao
import com.flexify.app.data.database.dao.ArtistDao
import com.flexify.app.data.database.dao.DownloadDao
import com.flexify.app.data.database.dao.GenreDao
import com.flexify.app.data.database.dao.LyricDao
import com.flexify.app.data.database.dao.PlaylistDao
import com.flexify.app.data.database.dao.RadioDao
import com.flexify.app.data.database.dao.SongDao
import com.flexify.app.data.database.dao.SyncActionDao
import com.flexify.app.data.database.entities.AlbumEntity
import com.flexify.app.data.database.entities.ArtistEntity
import com.flexify.app.data.database.entities.DownloadEntity
import com.flexify.app.data.database.entities.GenreEntity
import com.flexify.app.data.database.entities.LyricEntity
import com.flexify.app.data.database.entities.PlaylistEntity
import com.flexify.app.data.database.entities.PlaylistSongCrossRef
import com.flexify.app.data.database.entities.RadioEntity
import com.flexify.app.data.database.entities.SongEntity
import com.flexify.app.data.database.entities.SyncActionEntity

@Database(
	version = 15,
	entities = [
		AlbumEntity::class,
		GenreEntity::class,
		PlaylistEntity::class,
		PlaylistSongCrossRef::class,
		SongEntity::class,
		ArtistEntity::class,
		RadioEntity::class,
		LyricEntity::class,
		SyncActionEntity::class,
		DownloadEntity::class
	]
)
@TypeConverters(Converters::class)
@ConstructedBy(CacheDatabaseConstructor::class)
abstract class CacheDatabase : RoomDatabase() {
	abstract fun albumDao(): AlbumDao
	abstract fun genreDao(): GenreDao
	abstract fun downloadDao(): DownloadDao
	abstract fun playlistDao(): PlaylistDao
	abstract fun songDao(): SongDao
	abstract fun artistDao(): ArtistDao
	abstract fun radioDao(): RadioDao
	abstract fun lyricDao(): LyricDao
	abstract fun syncActionDao(): SyncActionDao
}

@Suppress("KotlinNoActualForExpect")
expect object CacheDatabaseConstructor : RoomDatabaseConstructor<CacheDatabase> {
	override fun initialize(): CacheDatabase
}

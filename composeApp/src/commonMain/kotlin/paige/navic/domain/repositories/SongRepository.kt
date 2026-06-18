package com.flexify.app.domain.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.flexify.app.domain.manager.SyncManager
import com.flexify.app.data.database.dao.AlbumDao
import com.flexify.app.data.database.dao.DownloadDao
import com.flexify.app.data.database.dao.SongDao
import com.flexify.app.data.database.entities.SyncActionType
import com.flexify.app.data.database.mappers.toDomainModel
import com.flexify.app.data.database.mappers.toEntity
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.domain.models.DomainSongListType
import com.flexify.app.ui.core.UiState
import com.flexify.app.util.core.sortedByListType
import kotlin.time.Clock

class SongRepository(
	private val songDao: SongDao,
	private val albumDao: AlbumDao,
	private val downloadDao: DownloadDao,
	private val dbRepository: DbRepository,
	private val syncManager: SyncManager
) {
	suspend fun getAllSongs(): List<DomainSong> {
		return songDao.getAllSongs().map { it.toDomainModel() }
	}

	private suspend fun getLocalData(
		listType: DomainSongListType,
		reversed: Boolean,
		artistId: String? = null
	): ImmutableList<DomainSong> {
		val songs = songDao
			.getAllSongs()
			.map { it.toDomainModel() }
		val filtered = if (artistId != null) {
			songs.filter { it.artistId == artistId }
		} else {
			songs
		}.toImmutableList().sortedByListType(
			listType,
			downloads = downloadDao.getAllDownloadsList(),
			albums = albumDao.getAllAlbumsList().map { it.toDomainModel() }
		)

		return if (reversed) {
			filtered.reversed().toImmutableList()
		} else {
			filtered
		}
	}

	private suspend fun refreshLocalData(
		listType: DomainSongListType,
		reversed: Boolean,
		artistId: String? = null
	): ImmutableList<DomainSong> {
		dbRepository.syncLibrarySongs().getOrThrow()
		return getLocalData(listType, reversed, artistId)
	}

	fun getSongsFlow(
		fullRefresh: Boolean,
		listType: DomainSongListType,
		reversed: Boolean,
		artistId: String? = null
	): Flow<UiState<ImmutableList<DomainSong>>> = flow {
		val localData = getLocalData(listType, reversed, artistId)
		if (fullRefresh) {
			emit(UiState.Loading(data = localData))
			try {
				emit(UiState.Success(data = refreshLocalData(listType, reversed, artistId)))
			} catch (error: Exception) {
				emit(UiState.Error(error = error, data = localData))
			}
		} else {
			emit(UiState.Success(data = localData))
		}
	}.flowOn(Dispatchers.IO)

	suspend fun isSongStarred(song: DomainSong) = songDao.isSongStarred(song.id)
	suspend fun getSongRating(song: DomainSong) = songDao.getSongRating(song.id) ?: 0
	suspend fun starSong(song: DomainSong) {
		val starredEntity = song.toEntity().copy(
			starredAt = Clock.System.now()
		)
		songDao.insertSong(starredEntity)
		syncManager.enqueueAction(SyncActionType.STAR, song.id)
	}

	suspend fun unstarSong(song: DomainSong) {
		val unstarredEntity = song.toEntity().copy(
			starredAt = null
		)
		songDao.insertSong(unstarredEntity)
		syncManager.enqueueAction(SyncActionType.UNSTAR, song.id)
	}

	suspend fun rateSong(song: DomainSong, rating: Int) {
		val ratedEntity = song.toEntity().copy(
			userRating = rating
		)
		songDao.insertSong(ratedEntity)
		when (rating) {
			0 -> syncManager.enqueueAction(SyncActionType.STAR_0, song.id)
			1 -> syncManager.enqueueAction(SyncActionType.STAR_1, song.id)
			2 -> syncManager.enqueueAction(SyncActionType.STAR_2, song.id)
			3 -> syncManager.enqueueAction(SyncActionType.STAR_3, song.id)
			4 -> syncManager.enqueueAction(SyncActionType.STAR_4, song.id)
			5 -> syncManager.enqueueAction(SyncActionType.STAR_5, song.id)
		}
	}
}

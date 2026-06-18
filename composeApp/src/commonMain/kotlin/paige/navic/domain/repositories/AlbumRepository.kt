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
import com.flexify.app.data.database.entities.DownloadStatus
import com.flexify.app.data.database.entities.SyncActionType
import com.flexify.app.data.database.mappers.toDomainModel
import com.flexify.app.data.database.mappers.toEntity
import com.flexify.app.domain.models.DomainAlbum
import com.flexify.app.domain.models.DomainAlbumListType
import com.flexify.app.ui.core.UiState
import com.flexify.app.util.core.toSqlQuery
import kotlin.time.Clock

class AlbumRepository(
	private val albumDao: AlbumDao,
	private val downloadDao: DownloadDao,
	private val syncManager: SyncManager,
	private val dbRepository: DbRepository
) {
	private suspend fun getLocalData(
		listType: DomainAlbumListType,
		reversed: Boolean
	): ImmutableList<DomainAlbum> {
		val downloadedSongIds = if (listType == DomainAlbumListType.Downloaded) {
			downloadDao.getAllDownloadsList()
				.filter { it.status == DownloadStatus.DOWNLOADED }
				.map { it.songId }
				.toSet()
		} else null

		return albumDao
			.getAlbumsByQuery(listType.toSqlQuery())
			.map { it.toDomainModel() }
			.let { if (reversed) it.asReversed() else it }
			.filter { album -> downloadedSongIds == null || downloadedSongIds.containsAll(album.songs.map { it.id }) }
			.toImmutableList()
	}

	private suspend fun refreshLocalData(
		listType: DomainAlbumListType,
		reversed: Boolean
	): ImmutableList<DomainAlbum> {
		dbRepository.syncLibrarySongs().getOrThrow()
		return getLocalData(listType, reversed)
	}

	fun getAlbumsFlow(
		fullRefresh: Boolean,
		listType: DomainAlbumListType,
		reversed: Boolean
	): Flow<UiState<ImmutableList<DomainAlbum>>> = flow {
		val localData = getLocalData(listType, reversed)
		if (fullRefresh) {
			emit(UiState.Loading(data = localData))
			try {
				emit(UiState.Success(data = refreshLocalData(listType, reversed)))
			} catch (error: Exception) {
				emit(UiState.Error(error = error, data = localData))
			}
		} else {
			emit(UiState.Success(data = localData))
		}
	}.flowOn(Dispatchers.IO)

	suspend fun isAlbumStarred(album: DomainAlbum) = albumDao.isAlbumStarred(album.id)
	suspend fun getAlbumRating(album: DomainAlbum) = albumDao.getAlbumRating(album.id) ?: 0

	suspend fun starAlbum(album: DomainAlbum) {
		val starredEntity = album.toEntity().copy(
			starredAt = Clock.System.now()
		)
		albumDao.insertAlbum(starredEntity)
		syncManager.enqueueAction(SyncActionType.STAR, album.id)
	}

	suspend fun unstarAlbum(album: DomainAlbum) {
		val unstarredEntity = album.toEntity().copy(
			starredAt = null
		)
		albumDao.insertAlbum(unstarredEntity)
		syncManager.enqueueAction(SyncActionType.UNSTAR, album.id)
	}

	suspend fun rateAlbum(album: DomainAlbum, rating: Int) {
		val ratedEntity = album.toEntity().copy(
			userRating = rating
		)
		albumDao.insertAlbum(ratedEntity)
		when (rating) {
			0 -> syncManager.enqueueAction(SyncActionType.STAR_0, album.id)
			1 -> syncManager.enqueueAction(SyncActionType.STAR_1, album.id)
			2 -> syncManager.enqueueAction(SyncActionType.STAR_2, album.id)
			3 -> syncManager.enqueueAction(SyncActionType.STAR_3, album.id)
			4 -> syncManager.enqueueAction(SyncActionType.STAR_4, album.id)
			5 -> syncManager.enqueueAction(SyncActionType.STAR_5, album.id)
		}
	}
}

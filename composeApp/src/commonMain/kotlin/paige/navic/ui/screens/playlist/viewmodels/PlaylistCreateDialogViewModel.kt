package com.flexify.app.ui.screens.playlist.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.flexify.app.data.database.dao.PlaylistDao
import com.flexify.app.data.database.mappers.toDomainModel
import com.flexify.app.data.database.mappers.toEntity
import com.flexify.app.domain.manager.SessionManager
import com.flexify.app.domain.models.DomainPlaylist
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.ui.core.UiState
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.notice_created_playlist
import com.flexify.app.domain.manager.SnackBarManager

class PlaylistCreateDialogViewModel(
	private val songs: List<DomainSong>,
	private val playlistDao: PlaylistDao,
	private val sessionManager: SessionManager,
	private val snackBarManager: SnackBarManager
) : ViewModel() {
	private val _creationState = MutableStateFlow<UiState<Nothing?>>(UiState.Success(null))
	val creationState = _creationState.asStateFlow()

	private val _events = Channel<Event>()
	val events = _events.receiveAsFlow()

	val name = TextFieldState()

	fun create() {
		viewModelScope.launch {
			_creationState.value = UiState.Loading()
			try {
				val playlist = sessionManager.api.createPlaylist(
					name = name.text.toString(),
					songIds = songs.map { it.id }
				)
				playlistDao.insertPlaylist(playlist.toEntity())
				_events.send(
					Event.Dismiss(
						playlistDao.getPlaylistById(playlist.id)!!.toDomainModel()
					)
				)
				_creationState.value = UiState.Success(null)
				snackBarManager.notify(com.flexify.app.generated.resources.Res.string.notice_created_playlist, playlist.name)
			} catch (e: Exception) {
				_creationState.value = UiState.Error(e)
			}
		}
	}

	sealed class Event {
		data class Dismiss(val playlist: DomainPlaylist) : Event()
	}
}

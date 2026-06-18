package com.flexify.app.ui.screens.song.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.domain.repositories.CollectionRepository
import com.flexify.app.ui.core.UiState

class SongDetailViewModel(
	songId: String,
	private val repository: CollectionRepository
) : ViewModel() {
	private val _songState = MutableStateFlow<UiState<DomainSong>>(UiState.Loading())
	val songState = _songState.asStateFlow()

	init {
		viewModelScope.launch {
			val song = repository.getSongById(songId)
			if (song != null) {
				_songState.value = UiState.Success(song)
			} else {
				_songState.value = UiState.Error(Exception("Unknown song"))
			}
		}
	}
}

package com.flexify.app.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.ui.components.dialogs.DeletionViewModel
import com.flexify.app.ui.components.sheets.ChangelogViewModel
import com.flexify.app.ui.screens.album.viewmodels.AlbumListViewModel
import com.flexify.app.ui.screens.artist.viewmodels.ArtistDetailViewModel
import com.flexify.app.ui.screens.artist.viewmodels.ArtistListViewModel
import com.flexify.app.ui.screens.collection.viewmodels.CollectionDetailViewModel
import com.flexify.app.ui.screens.genre.viewmodels.GenreListViewModel
import com.flexify.app.ui.screens.login.viewmodels.LoginViewModel
import com.flexify.app.ui.screens.lyrics.viewmodels.LyricsScreenViewModel
import com.flexify.app.ui.screens.nowPlaying.viewmodels.NowPlayingViewModel
import com.flexify.app.ui.screens.playlist.viewmodels.PlaylistCreateDialogViewModel
import com.flexify.app.ui.screens.playlist.viewmodels.PlaylistListViewModel
import com.flexify.app.ui.screens.playlist.viewmodels.PlaylistUpdateDialogViewModel
import com.flexify.app.ui.screens.queue.viewmodels.QueueViewModel
import com.flexify.app.ui.screens.radio.viewmodels.RadioCreateDialogViewModel
import com.flexify.app.ui.screens.radio.viewmodels.RadioListViewModel
import com.flexify.app.ui.screens.search.viewmodels.SearchViewModel
import com.flexify.app.ui.screens.settings.viewmodels.LyricsPriorityViewModel
import com.flexify.app.ui.screens.settings.viewmodels.NavtabsViewModel
import com.flexify.app.ui.screens.settings.viewmodels.SettingsDataStorageViewModel
import com.flexify.app.ui.screens.share.viewmodels.ShareDialogViewModel
import com.flexify.app.ui.screens.share.viewmodels.ShareListViewModel
import com.flexify.app.ui.screens.song.viewmodels.SongDetailViewModel
import com.flexify.app.ui.screens.song.viewmodels.SongListViewModel

val viewModelModule = module {
	viewModel { (artistId: String) ->
		ArtistDetailViewModel(
			artistId = artistId,
			repository = get(),
			artistRepository = get(),
			songRepository = get(),
			albumRepository = get(),
			artistDao = get(),
			albumDao = get(),
			downloadManager = get(),
			snackBarManager = get(),
			connectivityManager = get()
		)
	}

	viewModel { (song: DomainSong?) ->
		LyricsScreenViewModel(
			song = song,
			repository = get()
		)
	}

	viewModel { (songs: List<DomainSong>, playlistToExclude: String?) ->
		PlaylistUpdateDialogViewModel(
			songs = songs,
			playlistToExclude = playlistToExclude,
			sessionManager = get(),
			snackBarManager = get()
		)
	}

	viewModelOf(::AlbumListViewModel)
	viewModel { params ->
		SongListViewModel(
			initialListType = get(),
			artistId = params.getOrNull(),
			repository = get(),
			downloadManager = get(),
			connectivityManager = get(),
			sessionManager = get()
		)
	}
	viewModelOf(::ArtistListViewModel)
	viewModelOf(::SearchViewModel)
	viewModelOf(::GenreListViewModel)
	viewModelOf(::RadioListViewModel)
	viewModelOf(::RadioCreateDialogViewModel)
	viewModelOf(::PlaylistListViewModel)
	viewModelOf(::LoginViewModel)
	viewModelOf(::QueueViewModel)
	viewModelOf(::ShareListViewModel)
	viewModelOf(::DeletionViewModel)
	viewModelOf(::ShareDialogViewModel)
	viewModel { (songs: List<DomainSong>) ->
		PlaylistCreateDialogViewModel(
			songs = songs,
			playlistDao = get(),
			sessionManager = get(),
			snackBarManager = get()
		)
	}
	viewModel { params ->
		CollectionDetailViewModel(
			collectionId = params.get(),
			repository = get(),
			songRepository = get(),
			albumRepository = get(),
			downloadManager = get(),
			sessionManager = get(),
			snackBarManager = get(),
			connectivityManager = get()
		)
	}
	viewModelOf(::SongDetailViewModel)
	viewModelOf(::SettingsDataStorageViewModel)
	viewModelOf(::ChangelogViewModel)
	viewModel { params ->
		NowPlayingViewModel(
			player = params.get(),
			songRepository = get()
		)
	}
	viewModelOf(::NavtabsViewModel)
	viewModelOf(::LyricsPriorityViewModel)
}

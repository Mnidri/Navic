package com.flexify.app.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.flexify.app.domain.repositories.AlbumRepository
import com.flexify.app.domain.repositories.ArtistRepository
import com.flexify.app.domain.repositories.CollectionRepository
import com.flexify.app.domain.repositories.DbRepository
import com.flexify.app.domain.repositories.GenreRepository
import com.flexify.app.domain.repositories.LyricsRepository
import com.flexify.app.domain.repositories.PlaylistRepository
import com.flexify.app.domain.repositories.RadioRepository
import com.flexify.app.domain.repositories.SearchRepository
import com.flexify.app.domain.repositories.ShareRepository
import com.flexify.app.domain.repositories.SongRepository

val repositoryModule = module {
	singleOf(::AlbumRepository)
	singleOf(::ArtistRepository)
	singleOf(::DbRepository)
	singleOf(::GenreRepository)
	singleOf(::LyricsRepository)
	singleOf(::SearchRepository)
	singleOf(::ShareRepository)
	singleOf(::CollectionRepository)
	singleOf(::PlaylistRepository)
	singleOf(::SongRepository)
	singleOf(::RadioRepository)
}

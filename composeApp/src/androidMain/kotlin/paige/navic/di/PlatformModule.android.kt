package com.flexify.app.di

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.flexify.app.data.database.CacheDatabase
import com.flexify.app.data.database.DownloadDatabase
import com.flexify.app.domain.manager.ConnectivityManager
import com.flexify.app.domain.manager.LogManager
import com.flexify.app.domain.manager.ShareManager
import com.flexify.app.domain.manager.StorageManager
import com.flexify.app.domain.repositories.PlayerStateRepository
import com.flexify.app.shared.AndroidMediaPlayerViewModel
import com.flexify.app.shared.MediaPlayerViewModel

actual val platformModule = module {
	single<CacheDatabase> {
		val dbPath = androidApplication()
			.getDatabasePath("cache.db")
			.absolutePath
		Room
			.databaseBuilder<CacheDatabase>(get(), dbPath)
			.setDriver(BundledSQLiteDriver())
			.fallbackToDestructiveMigration(true)
			.build()
	}

	single<DownloadDatabase> {
		val dbPath = androidApplication()
			.getDatabasePath("downloads.db")
			.absolutePath
		Room
			.databaseBuilder<DownloadDatabase>(get(), dbPath)
			.setDriver(BundledSQLiteDriver())
			.fallbackToDestructiveMigration(true)
			.build()
	}

	single<PlayerStateRepository> {
		val context = androidApplication()
		val producePath = {
			context.filesDir.resolve(PlayerStateRepository.DATASTORE_FILE_NAME).absolutePath
		}
		PlayerStateRepository(PlayerStateRepository.getInstance(producePath))
	}

	single<MediaPlayerViewModel> {
		AndroidMediaPlayerViewModel(
			application = androidApplication(),
			stateRepository = get(),
			albumDao = get(),
			downloadManager = get(),
			connectivityManager = get(),
			sessionManager = get(),
			platformContext = get(),
			preferenceManager = get(),
			snackBarManager = get()
		)
	}

	singleOf(::ShareManager)
	singleOf(::StorageManager)
	singleOf(::ConnectivityManager)
	singleOf(::LogManager)
}

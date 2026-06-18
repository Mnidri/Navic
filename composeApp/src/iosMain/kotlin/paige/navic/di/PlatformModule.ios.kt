package com.flexify.app.di

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.flexify.app.data.database.CacheDatabase
import com.flexify.app.data.database.DownloadDatabase
import com.flexify.app.domain.manager.ConnectivityManager
import com.flexify.app.domain.manager.LogManager
import com.flexify.app.domain.manager.ShareManager
import com.flexify.app.domain.manager.StorageManager
import com.flexify.app.domain.repositories.PlayerStateRepository
import com.flexify.app.shared.IOSMediaPlayerViewModel
import com.flexify.app.shared.MediaPlayerViewModel
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import coil3.PlatformContext as CoilPlatformContext

actual val platformModule = module {
	single<CacheDatabase> {
		val dbPath = documentDirectory() + "/cache.db"
		Room
			.databaseBuilder<CacheDatabase>(dbPath)
			.setDriver(BundledSQLiteDriver())
			.fallbackToDestructiveMigration(true)
			.build()
	}

	single<DownloadDatabase> {
		val dbPath = documentDirectory() + "/downloads.db"
		Room
			.databaseBuilder<DownloadDatabase>(dbPath)
			.setDriver(BundledSQLiteDriver())
			.fallbackToDestructiveMigration(true)
			.build()
	}

	single<PlayerStateRepository> {
		val producePath = {
			@OptIn(ExperimentalForeignApi::class)
			val directory = NSFileManager.defaultManager.URLForDirectory(
				directory = NSDocumentDirectory,
				inDomain = NSUserDomainMask,
				appropriateForURL = null,
				create = true,
				error = null
			)
			directory?.path + "/${PlayerStateRepository.DATASTORE_FILE_NAME}"
		}
		PlayerStateRepository(PlayerStateRepository.getInstance(producePath))
	}

	viewModel<MediaPlayerViewModel> {
		IOSMediaPlayerViewModel(
			stateRepository = get(),
			downloadManager = get(),
			connectivityManager = get(),
			syncManager = get(),
			sessionManager = get(),
			preferenceManager = get(),
			snackBarManager = get()
		)
	}

	singleOf(::ShareManager)
	single<CoilPlatformContext> { CoilPlatformContext.INSTANCE }
	singleOf(::StorageManager)
	singleOf(::ConnectivityManager)
	singleOf(::LogManager)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
	val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
		directory = NSDocumentDirectory,
		inDomain = NSUserDomainMask,
		appropriateForURL = null,
		create = false,
		error = null,
	)
	return requireNotNull(documentDirectory?.path)
}

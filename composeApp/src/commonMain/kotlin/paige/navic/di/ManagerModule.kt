package com.flexify.app.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.flexify.app.domain.manager.DownloadManager
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.manager.SessionManager
import com.flexify.app.domain.manager.SleepTimerManager
import com.flexify.app.domain.manager.SnackBarManager
import com.flexify.app.domain.manager.SyncManager

val managerModule = module {
	singleOf(::SleepTimerManager)
	single(createdAtStart = true) {
		SyncManager(get(), get(), get(), get(), get(), get()).apply {
			startPeriodicSync()
		}
	}
	singleOf(::DownloadManager)
	singleOf(::SessionManager)
	singleOf(::PreferenceManager)
	singleOf(::SnackBarManager)
}

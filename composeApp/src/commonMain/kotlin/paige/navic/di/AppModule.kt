package com.flexify.app.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val appModule = module {
	single { Settings() }
}

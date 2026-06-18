package com.flexify.app.domain.manager

import kotlinx.coroutines.flow.StateFlow

expect class ConnectivityManager {
	val isCellular: StateFlow<Boolean>
	val isOnline: StateFlow<Boolean>
}

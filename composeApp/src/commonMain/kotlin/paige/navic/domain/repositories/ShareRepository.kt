package com.flexify.app.domain.repositories

import com.flexify.app.data.database.mappers.toDomainModel
import com.flexify.app.domain.manager.SessionManager

class ShareRepository(
	private val sessionManager: SessionManager
) {
	suspend fun getShares() = sessionManager.api.getShares().map { it.toDomainModel() }
}

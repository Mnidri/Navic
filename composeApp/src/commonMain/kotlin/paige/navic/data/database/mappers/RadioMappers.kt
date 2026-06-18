package com.flexify.app.data.database.mappers

import com.flexify.app.data.database.entities.RadioEntity
import com.flexify.app.domain.models.DomainRadio
import dev.zt64.subsonic.api.model.InternetRadioStation as ApiRadio

fun ApiRadio.toEntity() = RadioEntity(
	radioId = id,
	name = name,
	streamUrl = streamUrl,
	homepageUrl = homepageUrl
)

fun RadioEntity.toDomainModel() = DomainRadio(
	id = radioId,
	name = name,
	streamUrl = streamUrl,
	homepageUrl = homepageUrl
)

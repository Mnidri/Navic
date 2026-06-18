package com.flexify.app.ui.screens.collection.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.count_songs
import org.jetbrains.compose.resources.pluralStringResource
import com.flexify.app.domain.models.DomainSongCollection
import com.flexify.app.ui.theme.defaultFont

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollectionDetailScreenFooterRow(
	collection: DomainSongCollection
) {
	Text(
		buildString {
			append(
				pluralStringResource(
					com.flexify.app.generated.resources.Res.plurals.count_songs,
					collection.songCount,
					collection.songCount
				)
			)
			append(" • ")
			append(collection.duration.toString())
		},
		style = MaterialTheme.typography.titleSmall,
		fontFamily = defaultFont(round = 100f),
		color = MaterialTheme.colorScheme.onSurfaceVariant,
		modifier = Modifier.fillMaxWidth().padding(
			horizontal = 16.dp,
			vertical = 8.dp
		)
	)
}

package com.flexify.app.ui.screens.song.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.models.DomainSongListType
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Sort
import com.flexify.app.ui.components.layouts.TopBarButton
import com.flexify.app.ui.components.sheets.SortSheet

@Composable
fun SongListScreenSortButton(
	nested: Boolean,
	selectedSorting: DomainSongListType,
	onSetSorting: (listType: DomainSongListType) -> Unit,
	selectedReversed: Boolean,
	onSetReversed: (Boolean) -> Unit
) {
	val platformContext = LocalPlatformContext.current
	val entries = remember { DomainSongListType.entries.toImmutableList() }
	var expanded by remember { mutableStateOf(false) }
	if (!nested) {
		IconButton(onClick = {
			platformContext.clickSound()
			expanded = true
		}) {
			Icon(
				Icons.Outlined.Sort,
				contentDescription = null
			)
		}
	} else {
		TopBarButton({ expanded = true }) {
			Icon(
				Icons.Outlined.Sort,
				contentDescription = null
			)
		}
	}
	if (expanded) {
		SortSheet(
			entries = entries,
			onDismissRequest = { expanded = false },
			selectedSorting = selectedSorting,
			onSetSorting = onSetSorting,
			selectedReversed = selectedReversed,
			label = { stringResource(it.displayName) },
			onSetReversed = onSetReversed
		)
	}
}

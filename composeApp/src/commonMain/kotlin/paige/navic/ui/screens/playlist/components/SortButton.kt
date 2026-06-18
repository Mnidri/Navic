package com.flexify.app.ui.screens.playlist.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.flexify.app.domain.models.DomainPlaylistListType
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Sort
import com.flexify.app.ui.components.layouts.TopBarButton
import com.flexify.app.ui.components.sheets.SortSheet

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaylistListScreenSortButton(
	nested: Boolean,
	selectedSorting: DomainPlaylistListType,
	onSetSorting: (DomainPlaylistListType) -> Unit,
	selectedReversed: Boolean,
	onSetReversed: (Boolean) -> Unit
) {
	val platformContext = LocalPlatformContext.current
	val entries = remember { DomainPlaylistListType.entries.toImmutableList() }
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
			selectedSorting = selectedSorting,
			selectedReversed = selectedReversed,
			label = { stringResource(it.displayName) },
			onSetSorting = onSetSorting,
			onSetReversed = onSetReversed,
			onDismissRequest = { expanded = false }
		)
	}
}

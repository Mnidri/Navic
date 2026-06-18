package com.flexify.app.ui.screens.album.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.persistentListOf
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.models.DomainAlbumListType
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.Sort
import com.flexify.app.ui.components.layouts.TopBarButton
import com.flexify.app.ui.components.sheets.SortSheet
import com.flexify.app.ui.screens.library.components.label

@Composable
fun AlbumListScreenSortButton(
	nested: Boolean,
	selectedSorting: DomainAlbumListType,
	onSetSorting: (DomainAlbumListType) -> Unit,
	selectedReversed: Boolean,
	onSetReversed: (Boolean) -> Unit
) {
	val platformContext = LocalPlatformContext.current
	val entries = remember {
		persistentListOf(
			DomainAlbumListType.AlphabeticalByArtist,
			DomainAlbumListType.Frequent,
			DomainAlbumListType.Recent,
			DomainAlbumListType.Newest,
			DomainAlbumListType.Highest,
			DomainAlbumListType.Starred,
			DomainAlbumListType.Random,
			DomainAlbumListType.ByYear(),
			DomainAlbumListType.Downloaded
		)
	}
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
			label = { it.label() },
			onSetSorting = onSetSorting,
			onSetReversed = onSetReversed,
			onDismissRequest = { expanded = false }
		)
	}
}

package com.flexify.app.ui.components.layouts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.title_albums
import navic.composeapp.generated.resources.title_artists
import navic.composeapp.generated.resources.title_genres
import navic.composeapp.generated.resources.title_library
import navic.composeapp.generated.resources.title_playlists
import navic.composeapp.generated.resources.title_radios
import navic.composeapp.generated.resources.title_search
import navic.composeapp.generated.resources.title_songs
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalNavStack
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager
import com.flexify.app.domain.models.settings.NavbarConfig
import com.flexify.app.domain.models.settings.NavbarTab
import com.flexify.app.domain.models.settings.NavigationBarLabelVisibility
import com.flexify.app.domain.models.settings.NavigationBarStyle
import com.flexify.app.icons.Icons
import com.flexify.app.icons.filled.Album
import com.flexify.app.icons.filled.Artist
import com.flexify.app.icons.filled.Genre
import com.flexify.app.icons.filled.LibraryMusic
import com.flexify.app.icons.filled.Radio
import com.flexify.app.icons.outlined.Album
import com.flexify.app.icons.outlined.Artist
import com.flexify.app.icons.outlined.Genre
import com.flexify.app.icons.outlined.LibraryMusic
import com.flexify.app.icons.outlined.Note
import com.flexify.app.icons.outlined.PlaylistPlay
import com.flexify.app.icons.outlined.Radio
import com.flexify.app.icons.outlined.Search
import com.flexify.app.ui.components.common.animatedTabIconPainter
import com.flexify.app.ui.core.UiState
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.ui.screens.settings.viewmodels.NavtabsViewModel

private enum class NavItem(
	val destination: Screen,
	val icon: ImageVector,
	val iconUnselected: ImageVector = icon,
	val label: StringResource
) {
	LIBRARY(
		destination = Screen.Library(),
		icon = Icons.Filled.LibraryMusic,
		iconUnselected = Icons.Outlined.LibraryMusic,
		label = Res.string.title_library
	),
	ALBUMS(
		destination = Screen.AlbumList(),
		icon = Icons.Filled.Album,
		iconUnselected = Icons.Outlined.Album,
		label = Res.string.title_albums
	),
	PLAYLISTS(
		destination = Screen.PlaylistList(),
		icon = Icons.Outlined.PlaylistPlay,
		label = Res.string.title_playlists
	),
	ARTISTS(
		destination = Screen.ArtistList(),
		icon = Icons.Filled.Artist,
		iconUnselected = Icons.Outlined.Artist,
		label = Res.string.title_artists
	),
	SEARCH(
		destination = Screen.Search(),
		icon = Icons.Outlined.Search,
		iconUnselected = Icons.Outlined.Search,
		label = Res.string.title_search
	),
	GENRES(
		destination = Screen.GenreList(),
		icon = Icons.Filled.Genre,
		iconUnselected = Icons.Outlined.Genre,
		label = Res.string.title_genres
	),
	SONGS(
		destination = Screen.SongList(),
		icon = Icons.Outlined.Note,
		iconUnselected = Icons.Outlined.Note,
		label = Res.string.title_songs
	),
	RADIOS(
		destination = Screen.RadioList(),
		icon = Icons.Filled.Radio,
		iconUnselected = Icons.Outlined.Radio,
		label = Res.string.title_radios
	)
}

@Composable
fun BottomBar(
	modifier: Modifier = Modifier,
	containerColor: Color = NavigationBarDefaults.containerColor,
	windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
	enabled: Boolean = true
) {
	val viewModel = koinViewModel<NavtabsViewModel>()
	val backStack = LocalNavStack.current
	val platformContext = LocalPlatformContext.current
	val state by viewModel.state.collectAsState()
	val containerColor by animateColorAsState(containerColor)
	val tabs = ((state as? UiState.Success)?.data ?: NavbarConfig.default)
		.tabs.filter { tab -> tab.visible }
	val preferenceManager = koinInject<PreferenceManager>()

	AnimatedContent(
		preferenceManager.navigationBarStyle != NavigationBarStyle.Short
			&& platformContext.sizeClass.widthSizeClass <= WindowWidthSizeClass.Compact
			&& tabs.size > 1
	) {
		if (tabs.size < 2) return@AnimatedContent
		if (it) {
			NavigationBar(
				modifier = modifier,
				containerColor = containerColor,
				windowInsets = windowInsets
			) {
				tabs.forEach { tab ->
					val item = when (tab.id) {
						NavbarTab.Id.LIBRARY -> NavItem.LIBRARY
						NavbarTab.Id.ALBUMS -> NavItem.ALBUMS
						NavbarTab.Id.PLAYLISTS -> NavItem.PLAYLISTS
						NavbarTab.Id.ARTISTS -> NavItem.ARTISTS
						NavbarTab.Id.SEARCH -> NavItem.SEARCH
						NavbarTab.Id.GENRES -> NavItem.GENRES
						NavbarTab.Id.SONGS -> NavItem.SONGS
						NavbarTab.Id.RADIOS -> NavItem.RADIOS
					}
					val selected = backStack.lastOrNull() == item.destination

					NavigationBarItem(
						selected = selected,
						enabled = enabled,
						alwaysShowLabel = preferenceManager.navigationBarLabelVisibility
							== NavigationBarLabelVisibility.Always,
						onClick = {
							platformContext.clickSound()
							backStack.apply {
								clear()
								add(item.destination)
							}
						},
						icon = {
							if (selected) {
								val painter = animatedTabIconPainter(item.destination)
								if (painter != null) {
									Icon(painter = painter, null)
								} else {
									Icon(item.icon, null)
								}
							} else {
								Icon(item.iconUnselected, null)
							}
						},
						label = {
							Text(
								stringResource(item.label),
								maxLines = 1,
								autoSize = TextAutoSize.StepBased(
									minFontSize = 1.sp,
									maxFontSize = MaterialTheme.typography.labelMedium.fontSize
								)
							)
						}
					)
				}
			}
		} else {
			ShortNavigationBar(
				modifier = modifier,
				containerColor = containerColor
			) {
				tabs.forEach { tab ->
					val item = when (tab.id) {
						NavbarTab.Id.LIBRARY -> NavItem.LIBRARY
						NavbarTab.Id.ALBUMS -> NavItem.ALBUMS
						NavbarTab.Id.PLAYLISTS -> NavItem.PLAYLISTS
						NavbarTab.Id.ARTISTS -> NavItem.ARTISTS
						NavbarTab.Id.SEARCH -> NavItem.SEARCH
						NavbarTab.Id.GENRES -> NavItem.GENRES
						NavbarTab.Id.SONGS -> NavItem.SONGS
						NavbarTab.Id.RADIOS -> NavItem.RADIOS
					}
					val selected = backStack.last() == item.destination

					ShortNavigationBarItem(
						iconPosition = if (platformContext.sizeClass.widthSizeClass > WindowWidthSizeClass.Compact)
							NavigationItemIconPosition.Start
						else NavigationItemIconPosition.Top,
						selected = backStack.last() == item.destination,
						enabled = enabled,
						onClick = {
							platformContext.clickSound()
							backStack.apply {
								clear()
								add(item.destination)
							}
						},
						icon = {
							if (selected) {
								val painter = animatedTabIconPainter(item.destination)
								if (painter != null) {
									Icon(painter = painter, null)
								} else {
									Icon(item.icon, null)
								}
							} else {
								Icon(item.iconUnselected, null)
							}
						},
						label = {
							Text(stringResource(item.label))
						}
					)
				}
			}
		}
	}
}

package com.flexify.app.ui.screens.playlist.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_cancel
import com.flexify.app.composeapp.generated.resources.action_ok
import com.flexify.app.composeapp.generated.resources.option_playlist_name
import com.flexify.app.composeapp.generated.resources.title_create_playlist
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.flexify.app.LocalNavStack
import com.flexify.app.ui.navigation.Screen
import com.flexify.app.domain.models.DomainSong
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.PlaylistAdd
import com.flexify.app.ui.components.common.FormButton
import com.flexify.app.ui.components.dialogs.FormDialog
import com.flexify.app.ui.screens.playlist.viewmodels.PlaylistCreateDialogViewModel
import com.flexify.app.ui.core.UiState

@Composable
fun PlaylistCreateDialog(
	onDismissRequest: () -> Unit,
	onRefresh: () -> Unit,
	songs: ImmutableList<DomainSong> = persistentListOf(),
	navigateAfterwards: Boolean = true
) {
	val viewModel = koinViewModel<PlaylistCreateDialogViewModel>(
		key = songs.joinToString { it.id },
		parameters = { parametersOf(songs) }
	)
	val backStack = LocalNavStack.current
	val state by viewModel.creationState.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.events.collect { event ->
			when (event) {
				is PlaylistCreateDialogViewModel.Event.Dismiss -> {
					onDismissRequest()
					onRefresh()
					if (navigateAfterwards) {
						if (backStack.contains(Screen.NowPlaying)) {
							backStack.remove(Screen.NowPlaying)
						}
						backStack.add(Screen.CollectionDetail(event.playlist.id, "playlists"))
					}
				}
			}
		}
	}

	FormDialog(
		onDismissRequest = onDismissRequest,
		icon = { Icon(Icons.Outlined.PlaylistAdd, null) },
		title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.title_create_playlist)) },
		buttons = {
			FormButton(
				onClick = {
					viewModel.create()
				},
				enabled = state !is UiState.Loading && viewModel.name.text.isNotBlank(),
				color = MaterialTheme.colorScheme.primary
			) {
				if (state !is UiState.Loading) {
					Text(stringResource(com.flexify.app.generated.resources.Res.string.action_ok))
				} else {
					CircularProgressIndicator(
						modifier = Modifier.size(20.dp)
					)
				}
			}
			FormButton(
				onClick = {
					onDismissRequest()
				},
				enabled = state !is UiState.Loading,
				content = { Text(stringResource(com.flexify.app.generated.resources.Res.string.action_cancel)) }
			)
		},
		content = {
			(state as? UiState.Error)?.error?.let {
				SelectionContainer {
					Text("$it")
				}
			}
			TextField(
				state = viewModel.name,
				label = { Text(stringResource(com.flexify.app.generated.resources.Res.string.option_playlist_name)) },
				lineLimits = TextFieldLineLimits.SingleLine
			)
		}
	)
}

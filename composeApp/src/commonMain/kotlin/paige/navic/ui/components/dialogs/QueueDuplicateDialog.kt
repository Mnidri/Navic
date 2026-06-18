package com.flexify.app.ui.components.dialogs

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_cancel
import navic.composeapp.generated.resources.action_ok
import navic.composeapp.generated.resources.notice_queue_duplicate
import navic.composeapp.generated.resources.title_confirm
import org.jetbrains.compose.resources.stringResource
import com.flexify.app.icons.Icons
import com.flexify.app.icons.outlined.PlaylistAdd
import com.flexify.app.ui.components.common.FormButton

@Composable
fun QueueDuplicateDialog(
	onDismissRequest: () -> Unit,
	onConfirm: () -> Unit
) {
	FormDialog(
		onDismissRequest = onDismissRequest,
		icon = { Icon(Icons.Outlined.PlaylistAdd, contentDescription = null) },
		title = { Text(stringResource(Res.string.title_confirm)) },
		content = { Text(stringResource(Res.string.notice_queue_duplicate)) },
		buttons = {
			FormButton(
				onClick = {
					onConfirm()
					onDismissRequest()
				}
			) {
				Text(stringResource(Res.string.action_ok))
			}
			FormButton(
				onClick = onDismissRequest
			) {
				Text(stringResource(Res.string.action_cancel))
			}
		},
	)
}

package com.flexify.app.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.action_ok
import com.flexify.app.composeapp.generated.resources.sideloading_warning_description
import com.flexify.app.composeapp.generated.resources.sideloading_warning_link_mask
import com.flexify.app.composeapp.generated.resources.sideloading_warning_subtitle
import com.flexify.app.composeapp.generated.resources.sideloading_warning_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import com.flexify.app.LocalPlatformContext
import com.flexify.app.domain.manager.PreferenceManager

@Composable
fun SideloadingDialog() {
	val platformContext = LocalPlatformContext.current
	val preferenceManager = koinInject<PreferenceManager>()
	AlertDialog(
		title = { Text(stringResource(com.flexify.app.generated.resources.Res.string.sideloading_warning_title)) },
		text = {
			Column {
				Text(
					stringResource(com.flexify.app.generated.resources.Res.string.sideloading_warning_subtitle),
					fontWeight = FontWeight(600),
					color = MaterialTheme.colorScheme.onSurface
				)
				Text(buildAnnotatedString {
					append(stringResource(com.flexify.app.generated.resources.Res.string.sideloading_warning_description))
					append(" ")
					withLink(LinkAnnotation.Url("https://keepandroidopen.org/")) {
						append(stringResource(com.flexify.app.generated.resources.Res.string.sideloading_warning_link_mask))
					}
				})
			}
		},
		onDismissRequest = {},
		confirmButton = {
			Button(onClick = {
				platformContext.clickSound()
				preferenceManager.showedSideloadingWarning = true
			}) {
				Text(stringResource(com.flexify.app.generated.resources.Res.string.action_ok))
			}
		}
	)
}

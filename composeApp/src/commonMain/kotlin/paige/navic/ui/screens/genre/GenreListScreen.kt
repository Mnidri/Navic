package com.flexify.app.ui.screens.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.flexify.app.composeapp.generated.resources.Res
import com.flexify.app.composeapp.generated.resources.title_genres
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.flexify.app.LocalBottomBarScrollManager
import com.flexify.app.ui.components.snackbars.ErrorSnackbar
import com.flexify.app.ui.components.layouts.ArtGrid
import com.flexify.app.ui.components.layouts.NestedTopBar
import com.flexify.app.ui.components.layouts.PullToRefreshBox
import com.flexify.app.ui.components.layouts.RootBottomBar
import com.flexify.app.ui.components.layouts.RootTopBar
import com.flexify.app.ui.screens.genre.components.genreListScreenContent
import com.flexify.app.ui.screens.genre.viewmodels.GenreListViewModel
import com.flexify.app.ui.core.UiState
import com.flexify.app.util.ui.withoutTop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenreListScreen(
	nested: Boolean
) {
	val viewModel = koinViewModel<GenreListViewModel>()
	val genresState by viewModel.genresState.collectAsState()
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	Scaffold(
		topBar = {
			if (!nested) {
				RootTopBar(
					{ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_genres)) },
					scrollBehavior
				)
			} else {
				NestedTopBar({ Text(stringResource(com.flexify.app.generated.resources.Res.string.title_genres)) })
			}
		},
		bottomBar = {
			val scrollManager = LocalBottomBarScrollManager.current
			if (!nested) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { innerPadding ->
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = innerPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			finished = genresState !is UiState.Loading,
			onRefresh = { viewModel.refreshGenres(true) },
			key = genresState
		) {
			ArtGrid(
				modifier = if (!nested)
					Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
				else Modifier,
				contentPadding = innerPadding.withoutTop(),
				state = viewModel.gridState,
				verticalArrangement = if ((genresState as? UiState.Success)?.data?.isEmpty() == true)
					Arrangement.Center
				else Arrangement.spacedBy(12.dp)
			) {
				genreListScreenContent(state = genresState)
			}
		}
	}

	ErrorSnackbar(
		error = (genresState as? UiState.Error)?.error,
		onClearError = { viewModel.clearError() }
	)
}

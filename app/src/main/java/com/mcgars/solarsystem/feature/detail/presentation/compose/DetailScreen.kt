package com.mcgars.solarsystem.feature.detail.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mcgars.solarsystem.compose.AppScaffold
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.di.store.ComponentHolder
import com.mcgars.solarsystem.di.store.ComponentStore
import com.mcgars.solarsystem.feature.detail.di.DetailComponent
import com.mcgars.solarsystem.feature.detail.presentation.model.DetailViewModel
import com.mcgars.solarsystem.feature.detail.presentation.model.DetailViewState
import com.mcgars.solarsystem.feature.main.presentation.compose.PlanetIcon
import com.mcgars.solarsystem.feature.main.presentation.compose.StarsBackground


@Composable
fun DetailScreen(
    planetPosition: Int,
    componentHolder: ComponentHolder<DetailComponent> = ComponentStore.getComponent(planetPosition),
    detailComponent: DetailComponent = componentHolder.get(),
    detailViewModel: DetailViewModel = viewModel(factory = detailComponent.viewModelFactory())
) {
    AppScaffold(componentHolder) {
        DetailContent(detailViewModel)
    }
}

@Composable
fun DetailContent(
    detailViewModel: DetailViewModel
) {
    val state: DetailViewState by detailViewModel.state.collectAsState()

    when (state) {
        is DetailViewState.Loading -> Loading()
        is DetailViewState.Data -> Detail((state as DetailViewState.Data).planet)
    }
}

@Composable
fun Loading() {
    Box {
        Text(modifier = Modifier.align(Alignment.Center), text = "loading")
    }
}

@Composable
fun Detail(
    planet: Planet
) {
    val scrollState = rememberScrollState()
    Box {
        StarsBackground(Modifier
            // parallax effect by offset
            .graphicsLayer { translationY = (-scrollState.value * 0.18f) }
            .height(240.dp)
            .fillMaxSize()
        )
        PlanetIcon(
            planet, Modifier
                .padding(top = 20.dp)
                .graphicsLayer { translationY = (-scrollState.value * 0.50f) }
                .align(alignment = Alignment.TopCenter)
        )
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(top = 200.dp)
                .background(
                    MaterialTheme.colors.surface,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .fillMaxSize()
                .padding(all = 16.dp)
        ) {
            Text(text = stringResource(id = planet.description))
        }
    }
}
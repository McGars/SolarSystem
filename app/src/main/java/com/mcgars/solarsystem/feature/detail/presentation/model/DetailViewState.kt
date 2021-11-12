package com.mcgars.solarsystem.feature.detail.presentation.model

import com.mcgars.solarsystem.data.model.Planet


sealed class DetailViewState {
    class Data(val planet: Planet): DetailViewState()
    object Loading : DetailViewState()
}
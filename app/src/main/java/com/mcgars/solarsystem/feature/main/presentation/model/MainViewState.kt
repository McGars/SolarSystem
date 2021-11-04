package com.mcgars.solarsystem.feature.main.presentation.model

import com.mcgars.solarsystem.data.model.Planet


sealed class MainViewState {
    class Data(val values: List<Planet>): MainViewState()
    object Empty: MainViewState()
}
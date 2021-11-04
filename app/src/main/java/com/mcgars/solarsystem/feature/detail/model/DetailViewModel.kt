package com.mcgars.solarsystem.feature.detail.model

import androidx.lifecycle.ViewModel
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.feature.main.presentation.model.MainViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class DetailViewModel @Inject constructor(
    planet: Planet
) : ViewModel() {

    private val _state = MutableStateFlow(planet)

    val state: StateFlow<Planet>
        get() = _state

}
package com.mcgars.solarsystem.feature.main.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.domain.usecase.SolarSystemUseCase
import com.mcgars.solarsystem.feature.navigation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val solarSystemUseCase: SolarSystemUseCase,
    private val router: Router
) : ViewModel() {

    private val _state = MutableStateFlow<MainViewState>(MainViewState.Empty)

    val state: StateFlow<MainViewState>
        get() = _state

    fun loadPlanets() {
        viewModelScope.launch {
            val planets = solarSystemUseCase.getPlanets()
            val state = if (planets.isEmpty()) {
                MainViewState.Empty
            } else {
                MainViewState.Data(planets)
            }
            _state.emit(state)
        }
    }

    fun onPlanetClick(planetPosition: Int) {
        router.navigateTo(Screens.detail(planetPosition))
    }

}
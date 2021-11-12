package com.mcgars.solarsystem.feature.detail.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.domain.usecase.SolarSystemUseCase
import com.mcgars.solarsystem.feature.main.presentation.model.MainViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class DetailViewModel @Inject constructor(
    private val planetPosition: Int,
    private val solarSystemUseCase: SolarSystemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DetailViewState>(DetailViewState.Loading)

    val state: StateFlow<DetailViewState>
        get() = _state

    init {
        lodPlanet()
    }

    private fun lodPlanet() {
        viewModelScope.launch {
            val planet = solarSystemUseCase.getPlanets()[planetPosition]
            _state.emit(DetailViewState.Data(planet))
        }
    }

}
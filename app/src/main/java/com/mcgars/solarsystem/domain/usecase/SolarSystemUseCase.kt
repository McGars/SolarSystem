package com.mcgars.solarsystem.domain.usecase

import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.data.repository.SolarSystemRepository
import javax.inject.Inject


interface SolarSystemUseCase {

    fun getPlanets(): List<Planet>

    class Impl @Inject constructor(
        private val solarSystemRepository: SolarSystemRepository
    ) : SolarSystemUseCase {

        override fun getPlanets(): List<Planet> = solarSystemRepository.getPlanet()

    }

}
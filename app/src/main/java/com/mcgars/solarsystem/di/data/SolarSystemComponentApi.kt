package com.mcgars.solarsystem.di.data

import com.mcgars.solarsystem.domain.usecase.SolarSystemUseCase


interface SolarSystemComponentApi {
    fun solarSystemUseCase(): SolarSystemUseCase
}
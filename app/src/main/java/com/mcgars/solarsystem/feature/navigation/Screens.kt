package com.mcgars.solarsystem.feature.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.feature.detail.presentation.DetailFragment
import com.mcgars.solarsystem.feature.main.presentation.MainFragment

object Screens {
    fun main() = FragmentScreen { MainFragment.newInstance() }
    fun detail(planet: Planet) = FragmentScreen { DetailFragment.newInstance(planet) }
}
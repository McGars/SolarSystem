package com.mcgars.solarsystem.feature.navigation

import androidx.navigation.NavHostController
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.feature.detail.presentation.DetailFragment
import com.mcgars.solarsystem.feature.main.presentation.MainFragment

/**
 * Навигация для Cicerone
 */
object Screens {
    fun main() = FragmentScreen { MainFragment.newInstance() }
    fun detail(planetPosition: Int) = FragmentScreen { DetailFragment.newInstance(planetPosition) }
}

/**
 * Навигация для compose
 */
enum class ScreensCompose(val screenName: String) {
    Main("Main"),
    Detail("Detail"),
}

fun NavHostController.navigateToDetails(planetPosition: Int) {
    navigate(ScreensCompose.Detail.screenName + "/$planetPosition")
}
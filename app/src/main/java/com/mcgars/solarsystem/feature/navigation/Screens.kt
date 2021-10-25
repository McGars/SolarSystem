package com.mcgars.solarsystem.feature.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.mcgars.solarsystem.feature.main.MainFragment

object Screens {
    fun main() = FragmentScreen { MainFragment.newInstance() }
}
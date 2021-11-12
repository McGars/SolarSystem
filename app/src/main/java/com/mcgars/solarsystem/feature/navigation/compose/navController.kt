package com.mcgars.solarsystem.feature.navigation.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mcgars.solarsystem.feature.detail.presentation.compose.DetailScreen
import com.mcgars.solarsystem.feature.main.presentation.compose.MainScreen
import com.mcgars.solarsystem.feature.navigation.ScreensCompose

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreensCompose.Main.screenName
    ) {

        composable(
            route = ScreensCompose.Main.screenName,
        ) {
            MainScreen(navController)
        }

        composable(
            route = ScreensCompose.Detail.screenName + "/{position}",
            arguments = listOf(navArgument("position") { type = NavType.IntType })
        ) {
            val planetPosition: Int = it.arguments?.getInt("position") ?: throw NullPointerException("The param for key: position doesn't put")
            DetailScreen(planetPosition)
        }
    }
}
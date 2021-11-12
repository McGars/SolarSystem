package com.mcgars.solarsystem.feature.main.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController
import com.mcgars.solarsystem.compose.composeView
import com.mcgars.solarsystem.feature.navigation.compose.NavigationComponent


class MainFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        setContent {
            val navController = rememberNavController()
            NavigationComponent(navController)
        }
    }

}
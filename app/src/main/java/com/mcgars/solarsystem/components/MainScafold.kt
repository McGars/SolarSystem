package com.mcgars.solarsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.mcgars.solarsystem.ui.theme.SolarSystemTheme

@Composable
fun AppScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    SolarSystemTheme {
        Scaffold(
            content = content
        )
    }
}
package com.mcgars.solarsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.mcgars.solarsystem.di.store.ComponentHolder
import com.mcgars.solarsystem.ui.theme.SolarSystemTheme
import kotlinx.coroutines.DisposableHandle

@Composable
fun AppFragment(
    content: @Composable () -> Unit
) {
    SolarSystemTheme(
        content = content
    )
}

@Composable
fun AppScaffoldFragment(
    content: @Composable (PaddingValues) -> Unit
) {
    SolarSystemTheme {
        Scaffold(
            content = content
        )
    }
}

@Composable
fun AppScaffold(
    viewModel: ViewModel,
    componentHolder: ComponentHolder<*>,
    content: @Composable (PaddingValues) -> Unit
) {
    SolarSystemTheme {
        Scaffold(
            content = content
        )
        DisposableEffect(key1 = viewModel) {
            onDispose { componentHolder.clear() }
        }
    }
}
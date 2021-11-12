package com.mcgars.solarsystem.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import com.mcgars.solarsystem.di.store.ComponentHolder
import com.mcgars.solarsystem.ui.theme.SolarSystemTheme

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
    componentHolder: ComponentHolder<*>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    content: @Composable (PaddingValues) -> Unit
) {
    SolarSystemTheme {
        Scaffold(
            content = content
        )
        DisposableEffect(lifecycleOwner) {
            onDispose { componentHolder.clear() }
        }
    }
}
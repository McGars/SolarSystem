package com.mcgars.solarsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberRandomSampleImageUrl(
    seed: Int = 1,
    width: Int = 300,
    height: Int = width,
): String = remember { "" }
package com.mcgars.solarsystem.feature.main.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.mcgars.solarsystem.R

@Composable
fun StarsBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.background_of_panet),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}
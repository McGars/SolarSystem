package com.mcgars.solarsystem.feature.main.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mcgars.solarsystem.R
import com.mcgars.solarsystem.data.model.Planet

@Composable
fun ProfilePicture(
    planet: Planet,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(planet.text),
            color = MaterialTheme.colors.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        PlanetIcon(planet)
    }
}

@Composable
fun PlanetIcon(planet: Planet, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(planet.icon),
        contentDescription = null,
        modifier = modifier.size(150.dp),
    )
}

@Preview
@Composable
private fun ProfilePicturePreview() {
    ProfilePicture(
        planet = Planet(text = R.string.planet_sun, R.string.planet_sun_description, R.drawable.ic_sun),
        modifier = Modifier
    )
}
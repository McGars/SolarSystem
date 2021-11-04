package com.mcgars.solarsystem.data.repository

import com.mcgars.solarsystem.R
import com.mcgars.solarsystem.data.model.Planet
import javax.inject.Inject


interface SolarSystemRepository {
    fun getPlanet(): List<Planet>

    class Impl @Inject constructor() : SolarSystemRepository {

        override fun getPlanet(): List<Planet> = listOf(
            Planet(text = R.string.planet_sun, description = R.string.planet_sun_description, R.drawable.ic_sun),
            Planet(text = R.string.planet_mercury, description = R.string.planet_mercury_description, R.drawable.ic_mercury),
            Planet(text = R.string.planet_venera, description = R.string.planet_venera_description, R.drawable.ic_venera),
            Planet(text = R.string.planet_earth, description = R.string.planet_earth_description, R.drawable.ic_earth),
            Planet(text = R.string.planet_mars, description = R.string.planet_mars_description, R.drawable.ic_mars),
            Planet(text = R.string.planet_yuputer, description = R.string.planet_yuputer_description, R.drawable.ic_yupiter),
            Planet(text = R.string.planet_saturn, description = R.string.planet_saturn_description, R.drawable.ic_saturn),
            Planet(text = R.string.planet_uran, description = R.string.planet_uran_description, R.drawable.ic_uran),
            Planet(text = R.string.planet_neptun, description = R.string.planet_neptun_description, R.drawable.ic_neptun),
        )

    }
}
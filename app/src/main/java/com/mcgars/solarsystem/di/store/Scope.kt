package com.mcgars.solarsystem.di.store

import android.content.Context
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.di.AppComponent
import com.mcgars.solarsystem.di.createAppComponent
import com.mcgars.solarsystem.di.data.SolarSystemComponent
import com.mcgars.solarsystem.di.data.createSolarSystemComponent
import com.mcgars.solarsystem.feature.detail.di.DetailComponent
import com.mcgars.solarsystem.feature.detail.di.createDetailComponent
import com.mcgars.solarsystem.feature.main.di.MainComponent
import com.mcgars.solarsystem.feature.main.di.createMainComponent


object Scope {

    fun registerComponents(context: Context): Unit = with(ComponentStore) {
        register<AppComponent> { createAppComponent(context) }
        register<SolarSystemComponent> { createSolarSystemComponent() }
        register<MainComponent> { createMainComponent(
            appComponentApi = getComponent<AppComponent>().get(),
            solarSystemComponentApi = getComponent<SolarSystemComponent>().get()
        )}
        registerWithParam<DetailComponent, Planet> { planet -> createDetailComponent(planet) }
    }

}
package com.mcgars.solarsystem.di.store

import android.content.Context
import com.mcgars.solarsystem.di.AppComponent
import com.mcgars.solarsystem.di.createAppComponent
import com.mcgars.solarsystem.di.data.createSolarSystemComponent
import com.mcgars.solarsystem.feature.detail.di.DetailComponent
import com.mcgars.solarsystem.feature.detail.di.createDetailComponent
import com.mcgars.solarsystem.feature.main.di.createMainComponent


object Scope {

    fun registerComponents(context: Context): Unit = with(ComponentStorage) {
        register { createAppComponent(context) }
        register { createSolarSystemComponent() }
        register(alias = emptyList()) {
            createMainComponent(
                appComponentApi = getComponent<AppComponent>(),
                solarSystemComponentApi = getComponent()
            )
        }
        registerWithParam<DetailComponent, Int>(alias = emptyList())  { planetPosition ->
            createDetailComponent(
                planetPosition = planetPosition,
                solarSystemComponentApi = getComponent()
            )
        }
    }

}
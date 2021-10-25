package com.mcgars.solarsystem.di.store

import android.content.Context
import com.mcgars.solarsystem.di.AppComponent
import com.mcgars.solarsystem.di.createAppComponent
import com.mcgars.solarsystem.feature.main.di.MainComponent
import com.mcgars.solarsystem.feature.main.di.createMainComponent


object Scope {

    fun registerComponents(context: Context): Unit = with(ComponentStore) {
        register<AppComponent> { createAppComponent(context) }
        register<MainComponent> { createMainComponent(getComponent<AppComponent>().get()) }
    }

}
package com.mcgars.solarsystem

import android.app.Application
import com.mcgars.solarsystem.di.store.Scope


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Scope.registerComponents(this)
    }

}
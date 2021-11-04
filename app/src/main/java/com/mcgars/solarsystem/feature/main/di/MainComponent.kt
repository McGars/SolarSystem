package com.mcgars.solarsystem.feature.main.di

import androidx.lifecycle.ViewModelProvider
import com.mcgars.solarsystem.di.AppComponentApi
import com.mcgars.solarsystem.di.data.SolarSystemComponentApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelModule
import dagger.Component

fun createMainComponent(
    appComponentApi: AppComponentApi,
    solarSystemComponentApi: SolarSystemComponentApi
): MainComponent = DaggerMainComponent.factory().create(appComponentApi, solarSystemComponentApi)

@MainScope
@Component(
    dependencies = [
        AppComponentApi::class,
        SolarSystemComponentApi::class
    ],
    modules = [
        MainModule::class,
        ViewModelModule::class
    ]
)
interface MainComponent : ViewModelApi {

    @Component.Factory
    interface Factory {

        fun create(
            appComponentApi: AppComponentApi,
            solarSystemComponentApi: SolarSystemComponentApi
        ): MainComponent

    }

}
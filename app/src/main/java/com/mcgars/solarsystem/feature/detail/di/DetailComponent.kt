package com.mcgars.solarsystem.feature.detail.di

import com.mcgars.solarsystem.di.data.SolarSystemComponentApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component

fun createDetailComponent(planetPosition: Int, solarSystemComponentApi: SolarSystemComponentApi) =
    DaggerDetailComponent.factory().create(planetPosition, solarSystemComponentApi)

@DetailScope
@Component(
    dependencies = [
        SolarSystemComponentApi::class
    ],
    modules = [
        DetailModule::class,
        ViewModelModule::class
    ]
)
interface DetailComponent : ViewModelApi {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance planetPosition: Int,
            solarSystemComponentApi: SolarSystemComponentApi
        ): DetailComponent
    }

}
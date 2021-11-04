package com.mcgars.solarsystem.feature.detail.di

import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.di.viewmodel.ViewModelApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component

fun createDetailComponent(planet: Planet) = DaggerDetailComponent.factory().create(planet)

@DetailScope
@Component(
    dependencies = [],
    modules = [
        DetailModule::class,
        ViewModelModule::class
    ]
)
interface DetailComponent : ViewModelApi {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance planet: Planet
        ): DetailComponent
    }

}
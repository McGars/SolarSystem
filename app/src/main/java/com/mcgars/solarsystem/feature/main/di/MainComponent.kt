package com.mcgars.solarsystem.feature.main.di

import androidx.lifecycle.ViewModelProvider
import com.mcgars.solarsystem.di.AppComponentApi
import com.mcgars.solarsystem.di.viewmodel.ViewModelModule
import dagger.Component

fun createMainComponent(
    appComponentApi: AppComponentApi
): MainComponent = DaggerMainComponent.factory().create(appComponentApi)

@Component(
    dependencies = [AppComponentApi::class],
    modules = [
        MainModule::class,
        ViewModelModule::class
    ]
)
@MainScope
interface MainComponent {

    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {

        fun create(
            appComponentApi: AppComponentApi
        ): MainComponent

    }

}
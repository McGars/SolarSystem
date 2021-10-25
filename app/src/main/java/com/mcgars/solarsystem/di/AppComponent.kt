package com.mcgars.solarsystem.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.mcgars.solarsystem.data.viewmodel.ViewModelFactory
import com.mcgars.solarsystem.feature.navigation.di.NavigationModule
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

fun createAppComponent(context: Context): AppComponent =
    DaggerAppComponent.factory().create(context)

@Component(
    modules = [AppModule::class, NavigationModule::class]
)
@Singleton
interface AppComponent : AppComponentApi {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }

}

@Module
abstract class AppModule {

    @Binds
    abstract fun bindViewModelFactory(impl: ViewModelFactory): ViewModelProvider.Factory

}
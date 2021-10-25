package com.mcgars.solarsystem.feature.main.di

import androidx.lifecycle.ViewModel
import com.mcgars.solarsystem.di.viewmodel.ViewModelKey
import com.mcgars.solarsystem.feature.main.domain.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface MainModule {

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(impl: MainViewModel): ViewModel

}
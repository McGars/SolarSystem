package com.mcgars.solarsystem.di.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.mcgars.solarsystem.data.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(impl: ViewModelFactory): ViewModelProvider.Factory

}
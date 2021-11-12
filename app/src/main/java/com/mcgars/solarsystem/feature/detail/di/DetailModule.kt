package com.mcgars.solarsystem.feature.detail.di

import androidx.lifecycle.ViewModel
import com.mcgars.solarsystem.di.viewmodel.ViewModelKey
import com.mcgars.solarsystem.feature.detail.presentation.model.DetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface DetailModule {

    @DetailScope
    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    fun bindDetailViewModel(impl: DetailViewModel): ViewModel

}
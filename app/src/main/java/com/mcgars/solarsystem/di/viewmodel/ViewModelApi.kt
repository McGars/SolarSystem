package com.mcgars.solarsystem.di.viewmodel

import androidx.lifecycle.ViewModelProvider


interface ViewModelApi {
    fun viewModelFactory(): ViewModelProvider.Factory
}
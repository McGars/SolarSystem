package com.mcgars.solarsystem.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router


interface AppComponentApi {
    fun context(): Context
    fun navigationHolder(): NavigatorHolder
    fun router(): Router
}
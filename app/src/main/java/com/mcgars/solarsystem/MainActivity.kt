package com.mcgars.solarsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.mcgars.solarsystem.compose.AppScaffoldFragment
import com.mcgars.solarsystem.databinding.ContentMainBinding
import com.mcgars.solarsystem.di.AppComponent
import com.mcgars.solarsystem.di.store.ComponentStorage
import com.mcgars.solarsystem.feature.navigation.Screens

class MainActivity : AppCompatActivity() {

    private val appComponent: AppComponent by lazy {
        ComponentStorage.getComponent()
    }

    private val navigatorHolder: NavigatorHolder by lazy {
        appComponent.navigationHolder()
    }

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScaffoldFragment {
                AndroidViewBinding(ContentMainBinding::inflate)
            }
        }

        appComponent.router().newRootScreen(Screens.main())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

}
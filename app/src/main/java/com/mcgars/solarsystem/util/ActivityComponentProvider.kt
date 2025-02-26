package com.mcgars.solarsystem.util

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mcgars.solarsystem.di.store.ComponentHolder
import com.mcgars.solarsystem.di.store.ComponentStorage
import kotlin.reflect.KClass

class ActivityComponentPropertyProvider<Component : Any>(
    private val component: KClass<Component>,
    private val params: (() -> Any)? = null,
    private val activity: FragmentActivity,
) : Lazy<Component>, DefaultLifecycleObserver {

    private var cached: ComponentHolder<Component>? = null

    private val viewModel by activity.viewModels<LifecycleViewModel>()

    override val value: Component
        get() {
            return cached?.get() ?: ComponentStorage.getComponent(component, params?.invoke()).also {
                cached = it
                activity.lifecycle.addObserver(this)
            }.get()
        }

    override fun isInitialized(): Boolean = cached != null

    override fun onCreate(owner: LifecycleOwner) {
        viewModel.addCloseable {
            cached?.clear()
            cached = null
        }
    }

}
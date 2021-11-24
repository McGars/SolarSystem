package com.mcgars.solarsystem.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mcgars.solarsystem.di.store.ComponentHolder
import com.mcgars.solarsystem.di.store.ComponentStorage
import kotlin.reflect.KClass


class ComponentProvider<Component : Any>(
    private val component: KClass<Component>,
    private val params: (() -> Any)? = null,
    private val owner: LifecycleOwner,
) : Lazy<Component>, DefaultLifecycleObserver {

    private var cached: ComponentHolder<Component>? = null

    override val value: Component
        get() {
            return cached?.get() ?: ComponentStorage.getComponent(component, params?.invoke()).also {
                cached = it
                owner.lifecycle.addObserver(this)
            }.get()
        }

    override fun isInitialized(): Boolean = cached != null

    override fun onDestroy(owner: LifecycleOwner) {
        cached?.clear()
        cached = null
    }

}
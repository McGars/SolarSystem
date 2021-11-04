package com.mcgars.solarsystem.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 *  Returns a property delegate to access Dagger Component by scoped to this Fragment:
 *  class MyFragment : Fragment() {
 *      val myComponent: MyComponent by component()
 *  }
 */
@MainThread
inline fun <reified Component : Any> Fragment.component(noinline params: (() -> Any)? = null): Lazy<Component> =
    createComponentLazy(Component::class, params)

/**
 * Helper method
 */
@MainThread
fun <Component : Any> Fragment.createComponentLazy(
    component: KClass<Component>,
    params: (() -> Any)? = null
): Lazy<Component> = ComponentProvider(component, params, this)
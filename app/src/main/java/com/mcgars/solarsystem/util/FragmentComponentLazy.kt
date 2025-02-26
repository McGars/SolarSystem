package com.mcgars.solarsystem.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.reflect.KClass

/**
 *  Returns a property delegate to access Dagger Component by scoped to this Fragment:
 *  class MyFragment : Fragment() {
 *      val myComponent: MyComponent by component()
 *  }
 */
@MainThread
inline fun <reified Component : Any> Fragment.component(noinline params: (() -> Any)? = null): Lazy<Component> =
    createFragmentComponentLazy(Component::class, params)

/**
 *  Returns a property delegate to access Dagger Component by scoped to this Activity:
 *  class MyActivity : FragmentActivity() {
 *      val myComponent: MyComponent by component()
 *  }
 */
@MainThread
inline fun <reified Component : Any> FragmentActivity.component(noinline params: (() -> Any)? = null): Lazy<Component> =
    createActivityComponentLazy(Component::class, params)

/**
 * Helper method
 */
@MainThread
fun <Component : Any> FragmentActivity.createActivityComponentLazy(
    component: KClass<Component>,
    params: (() -> Any)? = null
): Lazy<Component> = ActivityComponentPropertyProvider(component, params, this)

/**
 * Helper method
 */
@MainThread
fun <Component : Any> Fragment.createFragmentComponentLazy(
    component: KClass<Component>,
    params: (() -> Any)? = null
): Lazy<Component> = FragmentComponentPropertyProvider(component, params, this)
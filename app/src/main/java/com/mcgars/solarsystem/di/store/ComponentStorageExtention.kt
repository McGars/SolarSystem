package com.mcgars.solarsystem.di.store

@Throws(NullPointerException::class)
inline fun <reified Component : Any> getComponent(params: Any? = null): Component =
    ComponentStorage.getComponent(params)

@Throws(NullPointerException::class)
inline fun <reified Component : Any> getComponentHolder(params: Any? = null): ComponentHolder<Component> =
    ComponentStorage.getComponentHolder(params)

@Throws(NullPointerException::class)
inline fun <reified Component : Any> ComponentStorage.get(params: Any? = null): Component =
    getComponent(params)
package com.mcgars.solarsystem.di.store

import kotlin.reflect.KClass


class ComponentHolder<Component : Any>(
    internal val parentComponent: Any?,
    internal val params: Any?,
    private val component: Component,
    private val componentKey: KClass<Component>,
) {

    fun get(): Component = component

    fun clear() {
        ComponentStore.clear(componentKey, params)
    }
}

class ComponentProvider<ParentComponent : Any?, Component : Any, Params : Any?>(
    val parentComponent: KClass<*>?,
    val provideComponent: (ParentComponent?, Params?) -> Component,
)

object ComponentStore {
    private val cache = mutableMapOf<Int, MutableMap<Int, ComponentHolder<*>>>()
    val providers = mutableMapOf<Int, ComponentProvider<Any?, Any, Any?>>()

    /**
     *
     */
    inline fun <reified Component : Any> register(
        noinline provider: () -> Component
    ) {
        val key = Component::class.hashCode()
        val wrapProvider = { _: Any?, _: Any? -> provider.invoke() }
        providers[key] = ComponentProvider(parentComponent = null, wrapProvider)
    }

    /**
     *
     */
    inline fun <reified Component : Any, Params : Any> registerWithParam(
        noinline provider: (params: Params) -> Component
    ) {
        val key = Component::class.hashCode()
        val wrapProvider = { _: Any?, params: Any? -> provider.invoke(params as Params) }
        providers[key] = ComponentProvider(parentComponent = null, wrapProvider)
    }

    /**
     *
     */
    inline fun <reified ParentComponent : Any, reified Component : Any> register(
        noinline provider: (component: ParentComponent) -> Component
    ) {
        val key = Component::class.hashCode()
        val wrapProvider =
            { parentComponent: Any?, _: Any? -> provider.invoke(requireNotNull(parentComponent) as ParentComponent) }
        providers[key] = ComponentProvider(ParentComponent::class, wrapProvider)
    }

    /**
     *
     */
    inline fun <reified ParentComponent, reified Component : Any, Params : Any?> register(
        noinline provider: (component: ParentComponent, params: Params) -> Component
    ) {
        val key = Component::class.hashCode()
        providers[key] = ComponentProvider(ParentComponent::class, provider as (Any?, Any?) -> Any)
    }

    /**
     *
     */
    inline fun <reified Component : Any> getComponent(params: Any? = null): ComponentHolder<Component> {
        return getComponent(Component::class, params)
    }

    /**
     *
     */
    fun <Component : Any> getComponent(
        component: KClass<Component>,
        params: Any? = null
    ): ComponentHolder<Component> {
        val key = component.hashCode()
        val componentBucket = cache[key] ?: createCacheBucked(component, params)
        val componentHolder = componentBucket[params.hashCode()] ?: createComponentHolder(
            componentKey = component,
            params = params,
            componentProvider = component.provider()
        )
        return componentHolder as ComponentHolder<Component>
    }

    /**
     *
     */
    fun clear(
        component: KClass<*>,
        params: Any? = null
    ) {
        val componentKey = component.hashCode()
        val paramKey = params.hashCode()
        val cacheComponent = cache[component.hashCode()]
        cacheComponent?.remove(paramKey)
        if (cacheComponent?.isEmpty() == true) {
            cache.remove(componentKey)
        }

        // all component's children also need remove
        val candidatesToRemove = cache.mapNotNull { (componentKey, componentBucket) ->
            val holders = componentBucket.mapNotNull { (cacheParamKey, componentHolder) ->
                if (componentHolder.parentComponent == component) cacheParamKey else null
            }
            holders.forEach(componentBucket::remove)
            if (componentBucket.isEmpty()) componentKey else null
        }
        candidatesToRemove.forEach(cache::remove)
    }

    /*
    *
    * */
    private fun <Component : Any> createCacheBucked(
        component: KClass<Component>,
        params: Any?
    ): MutableMap<Int, ComponentHolder<*>> {
        val key = component.hashCode()
        val componentHolder = createComponentHolder(component, params, component.provider())
        val paramKey = params.hashCode()
        return mutableMapOf(paramKey to componentHolder).also {
            cache[key] = it
        }
    }

    /*
    *
    * */
    private fun <Component : Any> createComponentHolder(
        componentKey: KClass<Component>,
        params: Any?,
        componentProvider: ComponentProvider<Any?, Any, Any?>
    ): ComponentHolder<*> {
        val parentComponent = componentProvider.parentComponent?.let {
            getComponent(component = it, params)
        }
        return ComponentHolder(
            parentComponent = parentComponent,
            params = params,
            component = componentProvider.provideComponent(parentComponent, params),
            componentKey = componentKey as KClass<Any>,
        )
    }

    private fun <Component : Any> KClass<Component>.provider(): ComponentProvider<Any?, Any, Any?> =
        providers[hashCode()] ?: throw NullPointerException("the provider must be registered")

}
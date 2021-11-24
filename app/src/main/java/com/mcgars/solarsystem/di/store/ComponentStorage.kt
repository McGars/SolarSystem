package com.mcgars.solarsystem.di.store

import kotlin.reflect.KClass

typealias Params = Any?

class ComponentHolder<Component : Any>(
    internal val params: Params,
    internal val component: Component,
    internal val componentClass: KClass<Component>,
) {

    fun get(): Component = component

    fun clear() {
        ComponentStorage.clear(componentClass, params)
    }
}

class ComponentProvider<ParentComponent : Any?, Component : Any, Params>(
    val parentComponent: ((Params) -> ComponentHolder<Any>)?,
    val provideComponent: (ParentComponent?, Params) -> Component,
)


object ComponentStorage {
    private val providers = mutableMapOf<Int, ComponentProvider<Any?, Any, Params>>()
    private val aliases = mutableMapOf<Int, KClass<*>>()
    private val cache = mutableMapOf<Int, MutableMap<Int, ComponentHolder<*>>>()
    private val indexes = mutableMapOf<Any, List<ComponentHolder<*>>>()

    /**
     * Component register in general container
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified Component : Any> register(
        alias: List<KClass<*>>? = null,
        noinline provider: () -> Component
    ) {
        val wrapProvider = { _: Any?, _: Params -> provider.invoke() }
        registerComponent(Component::class, alias, providerParentComponent = null, wrapProvider)
    }

    /**
     * Component register in general container
     *
     * When [getComponent] are invoked with params then they transmitted as parameter in [provider]
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified Component : Any, Params : Any> registerWithParam(
        alias: List<KClass<*>>? = null,
        noinline provider: (params: Params) -> Component
    ) {
        val wrapProvider = { _: Any?, params: Any? -> provider.invoke(params as Params) }
        registerComponent(Component::class, alias, providerParentComponent = null, wrapProvider)
    }

    /**
     * Component register in general container
     *
     * When [Component] dependencies of [ParentComponent] then [ParentComponent] transmitted as parameter in [provider]
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified ParentComponent : Any, reified Component : Any> register(
        alias: List<KClass<*>>? = null,
        noinline parentComponent: () -> ComponentHolder<ParentComponent>,
        noinline provider: (component: ParentComponent) -> Component
    ) {
        val wrapProvider =
            { parent: Any?, _: Params -> provider.invoke(requireNotNull(parent) as ParentComponent) }
        val wrapParentComponentProvider = { _: Params -> parentComponent.invoke() }
        registerComponent(
            Component::class,
            alias,
            wrapParentComponentProvider as ((Any?) -> ComponentHolder<Any>),
            wrapProvider
        )
    }

    /**
     * Component register in general container
     *
     * When [getComponent] are invoked with params and
     * when [Component] dependencies of [ParentComponent] then params and [ParentComponent]
     * transmitted as parameters in [provider]
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified ParentComponent : Any, reified Component : Any, Params : Any?> register(
        alias: List<KClass<*>>? = null,
        noinline parentComponent: (Params) -> ComponentHolder<ParentComponent>,
        noinline provider: (component: ParentComponent, params: Params) -> Component
    ) {
        registerComponent(
            Component::class,
            alias,
            parentComponent as ((Any?) -> ComponentHolder<Any>),
            provider as (Any?, Any?) -> Component
        )
    }

    /*private, not use*/
    fun <Component : Any> registerComponent(
        componentClass: KClass<Component>,
        alias: List<KClass<*>>? = null,
        providerParentComponent: ((Params) -> ComponentHolder<Any>)? = null,
        provider: (Any?, Params) -> Component,
    ) {
        val key = componentClass.hashCode()
        val ali = alias?.toTypedArray() ?: componentClass.java.interfaces
        ali.forEach {
            val aliasKey = it.hashCode()
            if (aliases.containsKey(aliasKey)) throw RuntimeException("alias $it already registered")
            aliases[aliasKey] = componentClass
        }

        providers[key] = ComponentProvider(providerParentComponent, provider)
    }

    /**
     * If [Component] not register in store, then will be thrown [NullPointerException]
     *
     * @param params any object, that transmitted in provider
     * @return [Component] registered by [register] method
     */
    @Throws(NullPointerException::class)
    inline fun <reified Component : Any> getComponent(params: Any? = null): ComponentHolder<Component> {
        return getComponent(Component::class, params)
    }

    /**
     * If [Component] not register in store, then will be thrown [NullPointerException]
     *
     * @param params any object, that transmitted in provider
     * @return [Component] registered by [register] method
     */
    fun <Component : Any> getComponent(
        component: KClass<Component>,
        params: Params = null
    ): ComponentHolder<Component> {
        val keyOrAliasKey = component.hashCode()
        val aliasComponent = aliases[keyOrAliasKey] ?: component
        val key = aliasComponent.hashCode()

        val componentBucket = cache[key] ?: createCacheBucked(aliasComponent, params)
        val componentHolder = componentBucket[params.hashCode()] ?: createComponentHolder(
            componentKey = aliasComponent,
            params = params,
            componentProvider = aliasComponent.provider()
        )
        return componentHolder as ComponentHolder<Component>
    }

    /**
     * Remove components from the store
     *
     * First clearing bucked with condition [params] and [component], if [params] not setted then
     * clearing bucked with condition [component]
     *
     * If [component] has children components, they also will be cleared
     */
    fun clear(
        component: KClass<*>,
        params: Any? = null
    ) {
        val componentKey = component.hashCode()
        val paramKey = params.hashCode()
        val cacheBucked = cache[componentKey]
        val componentHolder = cacheBucked?.remove(paramKey)
        if (cacheBucked?.isEmpty() == true) {
            cache.remove(componentKey)
        }

        if (componentHolder == null) return

        // all children components also cleared
        val children = indexes.remove(componentHolder)
        children?.forEach(ComponentHolder<*>::clear)
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
        params: Params,
        componentProvider: ComponentProvider<Any?, Any, Params>
    ): ComponentHolder<*> {
        val parentComponentHolder = componentProvider.parentComponent?.invoke(params)
        val componentHolder = ComponentHolder(
            params = params,
            component = componentProvider.provideComponent(parentComponentHolder, params),
            componentClass = componentKey as KClass<Any>,
        )

        // Index link create between the parent and with his children
        if (parentComponentHolder != null) {
            val index = indexes[parentComponentHolder] ?: listOf()
            indexes[parentComponentHolder] = index + componentHolder
        }

        return componentHolder
    }

    private fun <Component : Any> KClass<Component>.provider(): ComponentProvider<Any?, Any, Any?> =
        providers[hashCode()] ?: throw NullPointerException("the provider must be registered")

}
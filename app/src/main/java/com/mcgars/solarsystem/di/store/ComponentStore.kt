package com.mcgars.solarsystem.di.store

import androidx.compose.ui.unit.ExperimentalUnitApi
import kotlin.reflect.KClass

typealias Params = Any?

class ComponentHolder<Component : Any>(
    internal val params: Params,
    internal val parentComponentHolder: ComponentHolder<Any>?,
    internal val component: Component,
    internal val componentClass: KClass<Component>,
) {

    fun get(): Component = component

    fun clear() {
        ComponentStore.clear(componentClass, params)
    }
}

class ComponentProvider<ParentComponent : Any?, Component : Any, Params>(
    val parentComponent: ((Params) -> ComponentHolder<Any>)?,
    val provideComponent: (ParentComponent?, Params) -> Component,
)


object ComponentStore {
    private val cache = mutableMapOf<Int, MutableMap<Int, ComponentHolder<*>>>()
    val providers = mutableMapOf<Int, ComponentProvider<Any?, Any, Params>>()
    val aliases = mutableMapOf<Int, KClass<*>>()

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
        registerComponent(alias, providerParentComponent = null, wrapProvider)
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
        registerComponent(alias, providerParentComponent = null, wrapProvider)
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
        registerComponent(alias, wrapParentComponentProvider as ((Any?) -> ComponentHolder<Any>), wrapProvider)
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
            alias,
            parentComponent as ((Any?) -> ComponentHolder<Any>),
            provider as (Any?, Any?) -> Any
        )
    }

    /*private, not use*/
    inline fun <reified Component : Any> registerComponent(
        alias: List<KClass<*>>? = null,
        noinline providerParentComponent: ((Params) -> ComponentHolder<Any>)? = null,
        noinline provider: (Any?, Params) -> Component,
    ) {
        val key = Component::class.hashCode()
        val ali = alias?.toTypedArray() ?: Component::class.java.interfaces
        ali.forEach {
            val aliasKey = it.hashCode()
            if (aliases.containsKey(aliasKey)) throw RuntimeException("alias $it already registered")
            aliases[aliasKey] = Component::class
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
        val cacheComponentHolder = cache[componentKey]
        val removedComponentHolder = cacheComponentHolder?.remove(paramKey)
        if (cacheComponentHolder?.isEmpty() == true) {
            cache.remove(componentKey)
        }

        if (removedComponentHolder == null) return

        // all component's children also need remove
        val candidatesToRemove = mutableListOf<Pair<KClass<Any>, Params>>()
        cache.forEach { (_, componentBucket) ->
            componentBucket.forEach { (_, componentHolder) ->

                val parentComponent = componentHolder.parentComponentHolder
                if (parentComponent?.component == removedComponentHolder.component) {
                    candidatesToRemove += componentHolder.componentClass as KClass<Any> to componentHolder.params
                }
            }
        }
        candidatesToRemove.forEach { (parentClass, parentParams) ->
            clear(parentClass, parentParams)
        }
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
        val parentComponent = componentProvider.parentComponent?.invoke(params)
        return ComponentHolder(
            params = params,
            parentComponentHolder = parentComponent,
            component = componentProvider.provideComponent(parentComponent, params),
            componentClass = componentKey as KClass<Any>,
        )
    }

    private fun <Component : Any> KClass<Component>.provider(): ComponentProvider<Any?, Any, Any?> =
        providers[hashCode()] ?: throw NullPointerException("the provider must be registered")

}
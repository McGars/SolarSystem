package com.mcgars.solarsystem.di.store

import androidx.annotation.VisibleForTesting
import androidx.viewbinding.BuildConfig
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

typealias Params = Any?

/**
 *
 * ComponentStorage.register {
 *     DaggerMyComponent()
 *         .builder()
 *         .networkApi(get())
 *         .build()
 * }
 *
 * ComponentStorage.registerWithParent<SomeParentComponent, MyComponent> {
 *     DaggerMyComponent()
 *         .builder()
 *         .networkApi(get())
 *         .build()
 * }
 *
 * ComponentStorage.getComponent<MyComponent>()
 *
 * or
 *
 * getComponent<MyComponent>()
 *
 * or
 *
 * MyFragment : Fragment {
 *
 *     val component: MyComponent by component()
 *
 * }
 *
 */
object ComponentStorage {

    private val providers = ConcurrentHashMap<Int, ComponentProvider<Any?, Any, Params>>()

    private val creationChain by lazy { mutableSetOf<KClass<*>>() }

    /*
    * Alternative names for the component
    * */
    @VisibleForTesting
    val aliases = ConcurrentHashMap<Int, KClass<*>>()

    /*
    * Aliases that needed to exclude by auto extracting from component
    * */
    @VisibleForTesting
    val excludedAliases = CopyOnWriteArraySet<Int>()

    @VisibleForTesting
    val cache = ConcurrentHashMap<Int, MutableMap<Int, ComponentHolder<*>>>()

    /*
    * Indexing parent's component to clear children component (optimisation)
    * */
    @VisibleForTesting
    val indexes = ConcurrentHashMap<Any, List<ComponentHolder<*>>>()

    /**
     * Component registered in the general container
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified Component : Any> register(
        alias: List<KClass<*>>? = null,
        noinline provider: () -> Component,
    ) {
        val wrapProvider = { _: Any?, _: Params -> provider.invoke() }
        registerComponent(Component::class, alias, providerParentComponent = null, wrapProvider)
    }

    /**
     * Component registered in the general container
     *
     * When [Component] depends on [ParentComponent] then [ParentComponent] transmitted as parameter in [provider]
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified ParentComponent : Any, reified Component : Any> registerWithParent(
        alias: List<KClass<*>>? = null,
        noinline parentComponent: () -> ParentComponent = { get() },
        noinline provider: (component: ParentComponent) -> Component,
    ) {
        val wrapProvider =
            { parent: Any?, _: Params -> provider.invoke(requireNotNull(parent) as ParentComponent) }
        val wrapParentComponentProvider = { _: Params -> parentComponent.invoke() }
        registerComponent(
            Component::class,
            alias,
            wrapParentComponentProvider,
            wrapProvider
        )
    }

    /**
     * Component registered in the general container
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
        noinline provider: (params: Params) -> Component,
    ) {
        val wrapProvider = { _: Any?, params: Any? -> provider.invoke(params as Params) }
        registerComponent(Component::class, alias, providerParentComponent = null, wrapProvider)
    }

    /**
     * Component registered in the general container
     *
     * When [getComponent] are invoked with params and
     * when [Component] depends on [ParentComponent] then params and [ParentComponent]
     * transmitted as parameters in [provider]
     *
     * @param [alias] may register the component as alias interface, example: interface MySupperApi
     *                if alias is null then will be auto registered all interfaces of component as aliases
     *                To turn off auto registered aliases, just set empty list
     * @param [provider] lazy init component
     */
    inline fun <reified ParentComponent : Any, reified Component : Any, Params : Any?> registerWithParentAndParam(
        alias: List<KClass<*>>? = null,
        noinline parentComponent: (Params) -> ParentComponent = { get() },
        noinline provider: (component: ParentComponent, params: Params) -> Component,
    ) {
        registerComponent(
            Component::class,
            alias,
            parentComponent as ((Any?) -> Any),
            provider as (Any?, Any?) -> Component
        )
    }

    /*private, not use*/
    fun <Component : Any> registerComponent(
        componentClass: KClass<Component>,
        alias: List<KClass<*>>? = null,
        providerParentComponent: ((Params) -> Any)? = null,
        provider: (Any?, Params) -> Component,
    ) {
        val key = componentClass.hashCode()
        if (providers[key] != null) {
            throw RuntimeException("The component $componentClass is already registered")
        }
        val ali = alias?.toTypedArray() ?: componentClass.java.interfaces
        ali.forEach {
            val aliasKey = it.hashCode()
            if (excludedAliases.contains(aliasKey)) {
                return@forEach
            }
            if (aliases.containsKey(aliasKey)) {
                throw RuntimeException("The alias $it is already registered")
            }
            aliases[aliasKey] = componentClass
        }

        providers[key] = ComponentProvider(providerParentComponent, provider)
    }

    /**
     * If [Component] isn't register in store, then will be thrown [NullPointerException]
     *
     * !!! USE ONLY if component is singleton
     * @param params any object, that transmitted in provider
     * @return [Component] registered by [registerWithParent] method
     */
    @Throws(NullPointerException::class)
    inline fun <reified Component : Any> getComponent(params: Any? = null): Component {
        return getComponent(Component::class, params).get()
    }

    /**
     * If [Component] isn't register in store, then will be thrown [NullPointerException]
     *
     * [ComponentHolder] have right method to clear [Component]
     *
     * !!! USE on fragments with lifecycle, use [Fragment.component]
     * @param params any object, that transmitted in provider
     * @return [Component] registered by [registerWithParent] method
     */
    @Throws(NullPointerException::class)
    inline fun <reified Component : Any> getComponentHolder(params: Any? = null): ComponentHolder<Component> {
        return getComponent(Component::class, params)
    }

    /**
     * If [Component] isn't register in store, then will be thrown [NullPointerException]
     *
     * @param params any object, that transmitted in provider
     * @return [Component] registered by [registerWithParent] method
     */
    fun <Component : Any> getComponent(
        component: KClass<Component>,
        params: Params = null,
    ): ComponentHolder<Component> {
        val keyOrAliasKey = component.hashCode()
        val aliasComponent = aliases[keyOrAliasKey] ?: component
        val key = aliasComponent.hashCode()

        val componentBucket = cache[key] ?: createCacheBucked(aliasComponent, params)
        val componentHolder = componentBucket[params.hashCode()] ?: createComponentHolder(
            componentKey = aliasComponent,
            params = params,
            componentProvider = aliasComponent.provider()
        ).also { createdComponentHolder ->
            componentBucket[params.hashCode()] = createdComponentHolder
        }
        return componentHolder as ComponentHolder<Component>
    }

    /**
     * @param aliases that needed to exclude by auto extracting from component
     */
    fun excludeAliases(vararg aliases: KClass<*>) {
        aliases.forEach { excludedAliases.add(it.hashCode()) }
    }

    /**
     * Components remove from the store
     *
     * First clear bucked with condition [params] and [component], if [params] isn't set, then
     * clear bucked with condition [component]
     *
     * If [component] have children components, they also will be cleared
     *
     * @param onlyChildren only all children will be removed, but [component] stayed in cache
     */
    fun clear(
        component: KClass<*>,
        params: Any? = null,
        onlyChildren: Boolean = false,
    ) {
        val keyOrAliasKey = component.hashCode()
        val aliasComponent = aliases[keyOrAliasKey] ?: component
        val componentKey = aliasComponent.hashCode()
        val paramKey = params.hashCode()
        val cacheBucked = cache[componentKey]
        val componentHolder = if (onlyChildren) cacheBucked?.get(paramKey) else cacheBucked?.remove(paramKey)
        if (cacheBucked?.isEmpty() == true) {
            cache.remove(componentKey)
        }

        if (componentHolder == null) return

        // all children components also cleared
        val children = indexes.remove(componentHolder.get())
        children?.forEach(ComponentHolder<*>::clear)
    }

    /**
     * Not use, only for testing
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun clear() {
        providers.clear()
        aliases.clear()
        excludedAliases.clear()
        cache.clear()
        indexes.clear()
    }

    private fun <Component : Any> createCacheBucked(
        component: KClass<Component>,
        params: Any?,
    ): MutableMap<Int, ComponentHolder<*>> {
        val key = component.hashCode()
        val componentHolder = createComponentHolder(component, params, component.provider())
        val paramKey = params.hashCode()
        return mutableMapOf(paramKey to componentHolder).also {
            cache[key] = it
        }
    }

    private fun <Component : Any> createComponentHolder(
        componentKey: KClass<Component>,
        params: Params,
        componentProvider: ComponentProvider<Any?, Any, Params>,
    ): ComponentHolder<*> {
        if (BuildConfig.DEBUG) {
            if (creationChain.contains(componentKey)) {
                throw IllegalStateException(
                    "detect cyclic dependence:\n" +
                            "Creation chain: $creationChain\n" +
                            "Cyclic creation: $componentKey"
                )
            }
            creationChain.add(componentKey)
        }

        val parentComponent = componentProvider.parentComponent?.invoke(params)
        val componentHolder = ComponentHolder(
            params = params,
            component = componentProvider.provideComponent(parentComponent, params),
            componentClass = componentKey as KClass<Any>,
        )

        // Index link create between the parent and with his children
        if (parentComponent != null) {
            val index = indexes[parentComponent] ?: listOf()
            indexes[parentComponent] = index + componentHolder
        }

        if (BuildConfig.DEBUG) {
            creationChain.remove(componentKey)
        }

        return componentHolder
    }

    private fun <Component : Any> KClass<Component>.provider(): ComponentProvider<Any?, Any, Any?> =
        providers[hashCode()] ?: kotlin.run {
            val inExclude = excludedAliases.contains(hashCode())
            if (inExclude) {
                throw NullPointerException("the provider $this mark as excluded in excludedAliases, use another class / interface, to get component")
            } else {
                throw NullPointerException("the provider $this must be registered")
            }
        }
}

/**
 * Холдер для хранения компонента
 */
class ComponentHolder<Component : Any>(
    internal val params: Params,
    internal val component: Component,
    internal val componentClass: KClass<Component>,
) {

    fun get(): Component = component

    /**
     * @param onlyChildren only all children will be removed, but [component] stayed in cache
     */
    fun clear(onlyChildren: Boolean = false) {
        ComponentStorage.clear(componentClass, params, onlyChildren)
    }
}

class ComponentProvider<ParentComponent : Any?, Component : Any, Params>(
    val parentComponent: ((Params) -> ParentComponent)?,
    val provideComponent: (ParentComponent?, Params) -> Component,
)
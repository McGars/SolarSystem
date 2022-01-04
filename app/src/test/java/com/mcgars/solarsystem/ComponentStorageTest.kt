package com.mcgars.solarsystem

import com.mcgars.solarsystem.di.store.ComponentStorage
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class ComponentStorageTest {

    interface First
    interface Second
    interface Third

    class A
    class B : First
    class C : Second, Third

    @Before
    fun before() {
        ComponentStorage.clear()
    }

    @Test
    fun registerComponent() = with(ComponentStorage) {
        // action
        register { A() }
        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<A>()
        val component2 = getComponent<A>()
        val componentInCache = cache[A::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(component, component2)
        assertEquals(cache.size, 1)
        assert(aliases.isEmpty())
        assert(indexes.isEmpty())
    }

    @Test
    fun registerDoubleComponentError() = with(ComponentStorage) {
        // action
        register { A() }

        // verify
        assertThrows(RuntimeException::class.java) {
            register { A() }
        }
        Unit
    }

    @Test
    fun registerDoubleComponentAliasError() = with(ComponentStorage) {
        // mock
        class D : Second
        // action
        register { C() }

        // verify
        assertThrows(RuntimeException::class.java) {
            register { D() }
        }
        Unit
    }

    @Test
    fun registerComponentWithAutoAlias() = with(ComponentStorage) {
        // action
        register { B() }

        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<First>()
        val componentInCache = cache[B::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assert(component is B)
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 1)
        assert(indexes.isEmpty())
    }

    @Test
    fun registerComponentWithAutoAlias_FewAlias() = with(ComponentStorage) {
        // action
        register { C() }

        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<Second>()
        val component2 = getComponent<Third>()
        val componentInCache = cache[C::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(component2, componentInCache)
        assert(component2 == component)
        assert(component is C)
        assert(component2 is C)
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 2)
        assert(indexes.isEmpty())
    }

    @Test
    fun registerComponentWithCustomAlias() = with(ComponentStorage) {
        // action
        register(
            alias = listOf(Third::class),
            provider = { C() }
        )

        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<Third>()
        val componentInCache = cache[C::class.hashCode()]?.get(0)?.get()

        // verify
        assertThrows(NullPointerException::class.java) {
            getComponent<Second>()
        }
        assertEquals(component, componentInCache)
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 1)
        assert(indexes.isEmpty())
    }

    @Test
    fun registerComponentRemoveAlias() = with(ComponentStorage) {
        // action
        register(
            alias = listOf(),
            provider = { C() }
        )

        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<C>()
        val componentInCache = cache[C::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 0)
        assert(indexes.isEmpty())
    }

    @Test
    fun getComponentError() = with(ComponentStorage) {
        // action
        register(
            alias = listOf(),
            provider = { C() }
        )

        // verify
        assert(cache.isEmpty())
        assertThrows(NullPointerException::class.java) {
            getComponent<First>()
        }
        Unit
    }

    @Test
    fun registerComponentWithParent() = with(ComponentStorage) {
        // action
        register { A() }
        register<A, B>(
            parentComponent = { getComponent() },
            provider = { B() }
        )
        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<B>()
        val componentInCache = cache[B::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(cache.size, 2)
        assertEquals(aliases.size, 1)
        assertEquals(indexes.size, 1)

        // action
        val parentHolder = getComponentHolder<A>()
        val indexHolder = requireNotNull(indexes[parentHolder.get()])
        assertEquals(component, indexHolder.first().get())
    }

    @Test
    fun registerComponentsWithParent() = with(ComponentStorage) {
        // action
        register { A() }
        register<A, B>(
            parentComponent = { getComponent() },
            provider = { B() }
        )
        register<A, C>(
            parentComponent = { getComponent() },
            provider = { C() }
        )

        // verify
        assert(cache.isEmpty())

        // action
        val component = getComponent<B>()
        val component2 = getComponent<C>()
        val componentInCache = cache[B::class.hashCode()]?.get(0)?.get()
        val component2InCache = cache[C::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(component2, component2InCache)
        assertEquals(cache.size, 3)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 1)

        // action
        val parentHolder = getComponentHolder<A>()
        val indexHolder = requireNotNull(indexes[parentHolder.get()])
        assertEquals(indexHolder.size, 2)
        assertEquals(component, indexHolder[0].get())
        assertEquals(component2, indexHolder[1].get())
    }

    @Test
    fun componentClear() = with(ComponentStorage) {
        // action
        register { A() }

        // action
        val component = getComponent<A>()
        val componentInCache = cache[A::class.hashCode()]?.get(0)?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 0)
        assertEquals(indexes.size, 0)

        // action
        clear(A::class)

        // verify
        assertEquals(cache.size, 0)
        assertEquals(aliases.size, 0)
        assertEquals(indexes.size, 0)

        // action
        val componentHolder = getComponentHolder<A>()
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 0)
        assertEquals(indexes.size, 0)
        componentHolder.clear()

        // verify
        assertEquals(cache.size, 0)
        assertEquals(aliases.size, 0)
        assertEquals(indexes.size, 0)
    }

    @Test
    fun componentClearByAlias() = with(ComponentStorage) {
        // action
        register { B() }

        // action
        val component = getComponent<First>()

        // verify
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 1)

        // action
        clear(First::class)

        // verify
        assertEquals(cache.size, 0)
        assertEquals(aliases.size, 1)
    }

    @Test
    fun clearComponentsWithParent() = with(ComponentStorage) {
        // action
        register { A() }
        register<A, B>(
            parentComponent = { getComponent() },
            provider = { B() }
        )
        register<A, C>(
            parentComponent = { getComponent() },
            provider = { C() }
        )

        // action
        val component = getComponent<B>()
        val component2 = getComponent<C>()

        // verify
        assertEquals(cache.size, 3)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 1)

        // action
        clear(A::class)

        // verify
        assertEquals(cache.size, 0)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 0)

    }

    @Test
    fun clearComponentsWithParent2() = with(ComponentStorage) {
        // action
        register { A() }
        register<A, B>(
            parentComponent = { getComponent() },
            provider = { B() }
        )
        register<A, C>(
            parentComponent = { getComponent() },
            provider = { C() }
        )

        // action
        val component = getComponent<B>()
        val component2 = getComponent<C>()

        // verify
        assertEquals(cache.size, 3)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 1)

        // action
        clear(B::class)

        // verify
        assertEquals(cache.size, 2)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 1)

        // action
        clear(C::class)

        // verify
        assertEquals(cache.size, 1)
        assertEquals(aliases.size, 3)
        assertEquals(indexes.size, 1)
    }

    @Test
    fun getComponentsWithParams_andClear() = with(ComponentStorage) {
        // action
        registerWithParam<A, String> { param ->
            assertEquals(param, "1")
            A()
        }

        // action
        val component = getComponent<A>("1")
        val componentInCache = cache[A::class.hashCode()]?.get("1".hashCode())?.get()

        // verify
        assertEquals(component, componentInCache)
        assertEquals(cache.size, 1)

        // action
        clear(A::class)

        // verify
        assertEquals(cache.size, 1)

        // action
        clear(A::class, "1")

        // verify
        assertEquals(cache.size, 0)
    }

}
package com.mcgars.solarsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHost
import com.mcgars.solarsystem.di.store.ComponentStorage
import com.mcgars.solarsystem.di.viewmodel.ViewModelApi

/**
 * Получение di компонента в compose функции
 * ```
 * @Composable
 * internal fun Screen() {
 *     val component = getComponent<ScreenComponent>()
 * }
 * ```
 * Внимание! Очистка di компонента уже заложена внутри этой функции
 *
 * @param params параметры, по которым будет создан уникальный di component
 */
@Stable
@Composable
inline fun <reified Component : Any> getComponent(params: Any? = null): Component {
    val lifecycleOwner = LocalLifecycleOwner.current
    val holder by remember(lifecycleOwner, params) {
        mutableStateOf(ComponentStorage.getComponentHolder<Component>(params))
    }

    viewModel<LifecycleViewModel> {
        LifecycleViewModel().apply {
            val key = holder.get().hashCode()
            viewModelLinkToDiStore.set(key, this)
            addCloseable {
                if (viewModelLinkToDiStore.clear(key, this)) {
                    holder.clear()
                }
            }
        }
    }
    return holder.get()
}

class LifecycleViewModel : ViewModel()

/**
 * Получение di компонента в compose функции
 * ```
 * @Composable
 * internal fun Screen() {
 *     val component = getComponent<ScreenComponent>()
 * }
 * ```
 * Внимание! Очистка di компонента уже заложена внутри этой функции, если будет использоваться [NavHost]
 * то di компонент будет уничтожаться при уходе с текущего compose экрана.
 *
 * Если используется [Fragment] в качестве жизненного цикла,
 * di компонент будут чиститься после уничтожения [Fragment]
 *
 * [useLifecycle] если используется этот параметр значит что-то не так с архитектурой.
 * Если true, то привязывается к жизненному циклу [LocalLifecycleOwner] - это скорее всего [Fragment] или [NavHost] (di не переживает реконфигурацию),
 *            так же в случае реконфигурации viewModel остается прежней, а вот di пересоздается и могут быть баги из-за не совместимости
 * Если false, то завязывается на жизненный цикл [ViewModel] и это позволяет пережить di и viewModel реконфигурацию activity
 *
 * @param params параметры, по которым будет создан уникальный di component
 * @param useLifecycle способ привязки к жизненному циклу
 */
@Stable
@Composable
inline fun <reified Component : Any> getComponent(useLifecycle: Boolean = false, params: Any? = null): Component {
    return if (useLifecycle.not()) {
        getComponent(params)
    } else {
        val lifecycleOwner = LocalLifecycleOwner.current
        val holder by remember(lifecycleOwner, params) {
            mutableStateOf(ComponentStorage.getComponentHolder<Component>(params))
        }
        DisposableEffect(lifecycleOwner) {
            val onClear = { holder.clear() }
            val observer = if (lifecycleOwner is NavBackStackEntry) {
                NavBackLifecycleComponentObserver(lifecycleOwner.id, onClear)
            } else {
                LifecycleComponentObserver(onClear)
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                // для NavBackStackEntry не удаляем обсервер из lifecycle
                // так как onDispose выполняется раньше LifecycleObserver.onDestroy
                // removeObserver выполняется внутри NavBackLifecycleComponentObserver
                if (observer is LifecycleComponentObserver) {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        }
        holder.get()
    }
}

/**
 * Создание ViewModel прямо в параметрах compose функции
 * ```
 * @Composable
 * internal fun Screen(
 *     viewModel: ScreenViewModel = getViewModel<ScreenComponent, ScreenViewModel>(),
 * )
 * ```
 * Внимание! Очистка di компонента уже заложена внутри этой функции, если будет уничтожена ViewModel
 * то di компонент будет уничтожен вместе с ней.
 *
 * @param params параметры, по которым будет создан уникальный di component
 * @param initBlock блок, который будет вызван при инициализации ViewModel
 */
@Stable
@Composable
inline fun <reified Component : ViewModelApi, reified VM : ViewModel> getViewModel(
    params: Any? = null,
    crossinline initBlock: (VM) -> Unit = { },
): VM {
    return viewModel {
        val holder = ComponentStorage.getComponentHolder<Component>(params)
        val key = holder.get().hashCode()
        holder.get().viewModelFactory().create(VM::class.java).apply {
            viewModelLinkToDiStore.set(key, this)
            addCloseable {
                if (viewModelLinkToDiStore.clear(key, this)) {
                    holder.clear()
                }
            }
            initBlock.invoke(this)
        }
    }
}

val viewModelLinkToDiStore = ViewModelLinkToDiStore()

/**
 * Помогает работать в ситуации когда один Di component c несколькими viewModel
 * Di будет уничтожен, после уничтожения последней viewModel в DI scope
 */
class ViewModelLinkToDiStore {

    private val map = mutableMapOf<Int, MutableSet<Int>>()

    fun set(component: Int, viewModelInt: ViewModel) {
        val set = map[component] ?: mutableSetOf<Int>().apply {
            map[component] = this
        }
        set.add(viewModelInt.hashCode())
    }

    fun clear(component: Int, viewModel: ViewModel): Boolean {
        map[component]?.remove(viewModel.hashCode())
        val isEmpty = map[component].isNullOrEmpty()
        if (isEmpty) map.remove(component)
        return isEmpty
    }

    fun size(): Pair<Int, Int> = map.keys.size to map.values.size

}

/**
 * ```
 * @Composable
 * internal fun Screen(
 *     viewModel: ScreenViewModel = component.getViewModel(),
 * )
 * ```
 */
@Stable
@Composable
inline fun <reified VM : ViewModel> ViewModelApi.getViewModel(): VM =
    viewModel(factory = viewModelFactory())

/**
 * У compose [NavHost] навигации свой жизненый цикл для экранов
 * При навигации вперед и назад [DisposableEffect] создается заного для экрана и при этом снова идет передобавление [LifecycleObserver] внутрь пула [Lifecycle]
 *
 * у [NavBackStackEntry] есть id, который завязан на уникальность экрана
 * С помощью id и equals вновь созданный [LifecycleObserver] не будет добавлен в пулл [Lifecycle], так как он уже добавлен
 */
class NavBackLifecycleComponentObserver(
    private val id: String,
    private val onClear: () -> Unit,
) : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        onClear.invoke()
        owner.lifecycle.removeObserver(this)
    }

    override fun equals(other: Any?): Boolean = other is NavBackLifecycleComponentObserver && other.id == id

    override fun hashCode(): Int = id.hashCode()
}

class LifecycleComponentObserver(
    private val onClear: () -> Unit,
) : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        onClear.invoke()
        owner.lifecycle.removeObserver(this)
    }
}
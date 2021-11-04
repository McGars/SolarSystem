package com.mcgars.solarsystem.feature.main.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.mcgars.solarsystem.components.AppScaffoldFragment
import com.mcgars.solarsystem.components.composeView
import com.mcgars.solarsystem.feature.main.di.MainComponent
import com.mcgars.solarsystem.feature.main.presentation.model.MainViewModel
import com.mcgars.solarsystem.feature.main.presentation.compose.ProfilePicture
import com.mcgars.solarsystem.feature.main.presentation.compose.StarsBackground
import com.mcgars.solarsystem.feature.main.presentation.model.MainViewState
import com.mcgars.solarsystem.util.component
import kotlin.math.absoluteValue


class MainFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment = MainFragment()
    }

    private val mainComponent: MainComponent by component()

    private val mainViewModel: MainViewModel by viewModels {
        mainComponent.viewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        setContent {
            AppScaffoldFragment {
                Box {
                    StarsBackground(Modifier.fillMaxSize())
                    PageContent()
                }
            }
        }
        mainViewModel.loadPlanets()
    }

    @Composable
    fun PageContent() {
        val viewState: MainViewState by mainViewModel.state.collectAsState()
        when (viewState) {
            is MainViewState.Data -> HorizontalPagerWithOffsetTransition(viewState as MainViewState.Data)
            is MainViewState.Empty -> Empty()
        }
    }

    @Composable
    fun Empty() {
        Box {
            Text(text = "empty")
        }
    }

    @Composable
    fun HorizontalPagerWithOffsetTransition(
        data: MainViewState.Data
    ) {
        HorizontalPager(
            count = data.values.size,
            // Add 32.dp horizontal padding to 'center' the pages
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val planet = data.values[page]
            Card(
                backgroundColor = Color.DarkGray.copy(alpha = 0.5f),
                modifier = Modifier
                    .clickable { mainViewModel.onPlanetClick(planet) }
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Box {
                    ProfilePicture(
                        planet,
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    )
                }
            }
        }
    }

}
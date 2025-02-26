package com.mcgars.solarsystem.feature.detail.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcgars.solarsystem.compose.AppScaffoldFragment
import com.mcgars.solarsystem.compose.composeView
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.feature.detail.di.DetailComponent
import com.mcgars.solarsystem.feature.detail.presentation.compose.DetailContent
import com.mcgars.solarsystem.feature.detail.presentation.compose.DetailScreen
import com.mcgars.solarsystem.feature.detail.presentation.model.DetailViewModel
import com.mcgars.solarsystem.util.component


class DetailFragment : Fragment() {

    companion object {
        private const val ARG_PLANET_POSITION = "arg.planetPosition"

        fun newInstance(planetPosition: Int): Fragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PLANET_POSITION, planetPosition)
            }
        }
    }

    private val detailComponent: DetailComponent by component {
        requireArguments().getInt(ARG_PLANET_POSITION)
    }

    private val detailViewModel: DetailViewModel by viewModels { detailComponent.viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        setContent {
            AppScaffoldFragment {
                DetailContent(detailViewModel)
            }
        }
    }

}
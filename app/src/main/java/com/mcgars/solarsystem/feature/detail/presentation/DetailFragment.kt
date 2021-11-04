package com.mcgars.solarsystem.feature.detail.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcgars.solarsystem.components.AppScaffoldFragment
import com.mcgars.solarsystem.components.composeView
import com.mcgars.solarsystem.data.model.Planet
import com.mcgars.solarsystem.feature.detail.compose.DetailContent
import com.mcgars.solarsystem.feature.detail.di.DetailComponent
import com.mcgars.solarsystem.feature.detail.model.DetailViewModel
import com.mcgars.solarsystem.feature.main.presentation.compose.StarsBackground
import com.mcgars.solarsystem.util.component


class DetailFragment : Fragment() {

    companion object {
        private const val ARG_PLANET = "arg.planet"

        fun newInstance(planet: Planet): Fragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PLANET, planet)
            }
        }
    }

    private val detailComponent: DetailComponent by component {
        requireArguments().getParcelable<Planet>(ARG_PLANET)
            ?: NullPointerException("arg $ARG_PLANET is not set")
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
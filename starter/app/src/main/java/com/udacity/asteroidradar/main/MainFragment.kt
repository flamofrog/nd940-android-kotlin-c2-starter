package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(
                AsteroidDatabase.getInstance(requireActivity().application)
            )
        )[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentMainBinding.inflate(inflater)
        binding = fragmentBinding

        setHasOptionsMenu(true)

        // Setting up the adapter etc...
        prepareFragment()

        return fragmentBinding.root
    }

    private fun prepareFragment() {

        // Setup Asteroid Details Navigator
        viewModel.navigateToAsteroid.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.onAsteroidNavigated()
            }
        }

        // Setup the adapter
        val adapter = AsteroidAdapter(AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)
        })
        binding?.asteroidRecycler?.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_saved_menu -> {
                    AsteroidApiFilter.SHOW_SAVED
                }
                R.id.show_today_menu -> {
                    AsteroidApiFilter.SHOW_TODAY
                }
                else -> {
                    AsteroidApiFilter.SHOW_WEEK
                }
            }
        )
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}

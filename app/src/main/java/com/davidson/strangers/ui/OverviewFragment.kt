package com.davidson.strangers.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidson.strangers.BuildConfig
import com.davidson.strangers.MainActivity
import com.davidson.strangers.R
import com.davidson.strangers.adapter.RvStrangerViewAdapter
import com.davidson.strangers.adapter.bindImage
import com.davidson.strangers.databinding.FragmentOverviewBinding
import com.davidson.strangers.util.LocationUtil
import com.davidson.strangers.util.isNetworkAvailable
import com.davidson.strangers.viewmodels.OverviewModelFactory
import com.davidson.strangers.viewmodels.OverviewViewModel
import com.google.android.material.snackbar.Snackbar


class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    private lateinit var viewModel: OverviewViewModel

    private lateinit var locationUtil: LocationUtil

    private var isSentToSettings = false

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            run {
                if (isGranted) {
                    Toast.makeText(activity, "Yes, Granted", Toast.LENGTH_SHORT)
                    updateGps()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.permission_denied_msg,
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(R.string.settings) {
                            isSentToSettings = true
                            Toast.makeText(
                                activity,
                                "Grant Location Permission",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToAppSettings()
                        }.show()
                }
            }
        }
    }

    private fun goToAppSettings() {
        val showAppSettingsIntent = Intent()
        showAppSettingsIntent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        showAppSettingsIntent.data = uri
        showAppSettingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(showAppSettingsIntent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val overviewModelFactory = OverviewModelFactory(requireActivity().application)

        var isSearchingThroughDb = false

        viewModel = ViewModelProvider(this, overviewModelFactory)[OverviewViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        val rvAdapterForHome = RvStrangerViewAdapter().also {
            it.setOnclickListenerR { imageView, strangerPerson ->
                Toast.makeText(activity, strangerPerson.name, Toast.LENGTH_SHORT).show()
                val extras = FragmentNavigatorExtras(imageView to "StrangerDetailed")
                findNavController().navigate(
                    OverviewFragmentDirections.actionOverviewFragmentToDetailedFragment(
                        strangerPerson.id
                    ), extras
                )
            }
        }


        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapterForHome
//            Toast.makeText(activity,"Tired", Toast.LENGTH_SHORT).show()
        }



        viewModel.strangerList.observe(viewLifecycleOwner) {
            binding.showStranger.text = it.size.toString()
            if (it.isEmpty()) {
                if (isSearchingThroughDb) {
                    rvAdapterForHome.submitList(it)
                }
                isSearchingThroughDb = false
            } else {
                rvAdapterForHome.submitList(it)
            }
        }



        viewModel.location.observe(viewLifecycleOwner) {
//            Toast.makeText(this.activity, "Getting Location", Toast.LENGTH_SHORT).show()
            viewModel.getWeather(it)
        }

        viewModel.weather.observe(viewLifecycleOwner) {
            binding.tvWeatherLocationHome.text = it.cityName
            binding.tvWeatherAqi.text = it.aqi.toString()
            val temp = "${it.temp.toInt()} °C"
            binding.tvWeatherTemp.text = temp
            binding.tvWeatherDesc.text = it.weatherDescription
            bindImage(binding.ivWeatherHomeImg, getWeatherImgUrl(it.weatherIcon))
        }

        binding.searchViewHome.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchInStrangersList(binding.searchViewHome.query.toString())
                isSearchingThroughDb = true
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchInStrangersList(binding.searchViewHome.query.toString())
                isSearchingThroughDb = true
                return true
            }

        })

        locationUtil = LocationUtil.getInstance((activity as (MainActivity)))


        if (!viewModel.isWeatherLoaded()) {
            Toast.makeText(activity, "Weather Loading Started", Toast.LENGTH_SHORT).show()
            handleLocation()
            viewModel.weatherLoaded()
        }

        binding.refreshWeatherBtn.setOnClickListener {
            reloadWeather()
        }

        return binding.root
    }

    private fun reloadWeather() {
        Toast.makeText(activity, "Weather RELoading Started", Toast.LENGTH_SHORT).show()
        handleLocation()
        viewModel.weatherLoaded()
    }

    private fun handleLocation() {
        Log.d("tired", "Inside Handle Location")

        try {
            Log.d("tired", "Inside try Handle lcoation")

            if (isNetworkAvailable(requireContext())) {
                Log.d("tired", "Inside Network")
                if (locationUtil.checkLocationPermission()) {
                    Log.d("tired", "Inside checkLocationPermission")

                    if (locationUtil.checkLocationEnabled()) {
                        Log.d("tired", "Inside checkLocationEnabled")

                        updateGps()
                    } else {
                        Toast.makeText(activity, "Turn On the the Location", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                Toast.makeText(activity, "Internet Unavailable", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "Error while gettingLocation "+e.message, Toast.LENGTH_SHORT).show()
        }
    }


    fun updateGps() {
        Toast.makeText(activity, "Getting Location from GPS", Toast.LENGTH_SHORT).show()
        locationUtil.requestCurrentLocation {
//            val address = locationUtil.geoCoderConverter(it.latitude, it.longitude)
//            viewModel.address.postValue(address)
            viewModel.location.postValue(it)
            viewModel.weatherLoaded()
        }
    }

    fun getWeatherImgUrl(iconCode: String): String {
        val icon = iconCode ?: "r01d"
        return "https://www.weatherbit.io/static/img/icons/${icon}.png"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        locationUtil.stopRequestingLocation()
    }

    override fun onStart() {
        super.onStart()
        if (isSentToSettings) {
            if (locationUtil.isLocationUsable)
                updateGps()
            isSentToSettings = false
        }
    }

}
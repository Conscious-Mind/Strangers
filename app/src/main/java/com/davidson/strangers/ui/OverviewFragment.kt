package com.davidson.strangers.ui

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidson.strangers.MainActivity
import com.davidson.strangers.adapter.RvStrangerViewAdapter
import com.davidson.strangers.databinding.FragmentOverviewBinding
import com.davidson.strangers.util.LocationUtil
import com.davidson.strangers.viewmodels.OverviewModelFactory
import com.davidson.strangers.viewmodels.OverviewViewModel


class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    private lateinit var viewModel: OverviewViewModel

    private lateinit var locationUtil: LocationUtil


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val overviewModelFactory = OverviewModelFactory(requireActivity().application)

        viewModel = ViewModelProvider(this, overviewModelFactory)[OverviewViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val rvAdapterForHome = RvStrangerViewAdapter().also {
            it.setOnclickListenerR { strangerPerson ->
                Toast.makeText(activity, strangerPerson.name, Toast.LENGTH_SHORT).show()
                findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToDetailedFragment(strangerPerson.id))
            }
        }

        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapterForHome
//            Toast.makeText(activity,"Tired", Toast.LENGTH_SHORT).show()
        }

        if (!(activity as MainActivity).isInternetAvailable()) {
            Toast.makeText(this.activity, "Internet Unavailable", Toast.LENGTH_SHORT).show()
        }


        viewModel.strangerList.observe(viewLifecycleOwner) {
            binding.showStranger.text = it.size.toString()
            rvAdapterForHome.submitList(it)
        }

//        viewModel.address.observe(viewLifecycleOwner) {
//            val text = "${it?.subAdminArea?:"null"}, ${it.countryName?:"null"}"
//            binding.tvWeatherLocationHome.text = text
//            Toast.makeText(activity, "getting air quality...", Toast.LENGTH_SHORT).show()
//        }

        viewModel.location.observe(viewLifecycleOwner){
            viewModel.getWeather(it)
        }

        viewModel.weather.observe(viewLifecycleOwner){
            binding.tvWeatherLocationHome.text  = it.cityName
            binding.tvWeatherAqi.text = it.aqi.toString()
            val temp = "${it.temp.toInt()} °C"
            binding.tvWeatherTemp.text = temp
            binding.tvWeatherDesc.text = it.weatherDescription
        }

        locationUtil = LocationUtil.getInstance((activity as (MainActivity)))

        handleLocation()

        return binding.root
    }

    private fun handleLocation() {
        if (locationUtil.checkLocationPermission()) {
            if (locationUtil.checkLocationEnabled()) {
                updateGps()
            } else {
                Toast.makeText(activity, "Turn On the the Location", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationUtil.requestLocationPermission()
        }
    }

    fun updateGps() {
        Toast.makeText(activity, "Getting Location...", Toast.LENGTH_SHORT).show()
        locationUtil.requestCurrentLocation {
//            val address = locationUtil.geoCoderConverter(it.latitude, it.longitude)
//            viewModel.address.postValue(address)
            viewModel.location.postValue(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationUtil.stopRequestingLocation()
    }

}
package com.davidson.strangers.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.davidson.strangers.MainActivity
import com.davidson.strangers.adapter.bindImage
import com.davidson.strangers.databinding.FragmentDetailedBinding
import com.davidson.strangers.util.LocationUtil
import com.davidson.strangers.viewmodels.DetailedViewModel
import com.davidson.strangers.viewmodels.DetailedViewModelFactory


class DetailedFragment : Fragment() {

    private lateinit var binding: FragmentDetailedBinding
    private lateinit var viewModel: DetailedViewModel
    private lateinit var locationUtil: LocationUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailedBinding.inflate(inflater, container, false)

        val args: DetailedFragmentArgs by navArgs()

        val detailedModelFactory =
            DetailedViewModelFactory(args.strangerId, requireActivity().application)

        viewModel = ViewModelProvider(this, detailedModelFactory)[DetailedViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.strangerDetails.observe(viewLifecycleOwner) { strangerPerson ->

            bindImage(binding.ivDetailedPicture, strangerPerson.pictureUrl)
        }

        locationUtil = LocationUtil.getInstance((activity as (MainActivity)))

        viewModel.strangerDetails.value?.let { requestLocationCoordinates(it.houseCity) }

        viewModel.address.observe(viewLifecycleOwner){
            viewModel.getWeather(it)
        }

        viewModel.weather.observe(viewLifecycleOwner) {
            binding.tvLocationDetailed.text = it.cityName
            binding.tvWeatherAqiDetailed.text = it.aqi.toString()
            val temp = "${it.temp.toInt()} °C"
            binding.tvWeatherTempDetailed.text = temp
            binding.tvWeatherDescDetailed.text = it.weatherDescription
        }
        return binding.root
    }


    fun requestLocationCoordinates(citName: String) {
        val address = locationUtil.geoCoderConverter(citName)
        viewModel.address.postValue(address)
        Toast.makeText(activity, "Activated REquest", Toast.LENGTH_SHORT).show()
    }
}
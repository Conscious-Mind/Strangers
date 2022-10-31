package com.davidson.strangers.ui

import android.os.Bundle
import android.transition.TransitionInflater
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

        // Shared Transistion View

        val animationDetailedImage = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        ).also {
            it.duration = 500L

        }
        val animationToOverviewImage = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        ).also {
            it.duration = 2000L
            it.startDelay = 1000L
        }

        sharedElementEnterTransition = animationDetailedImage
        sharedElementReturnTransition = animationDetailedImage


        val args: DetailedFragmentArgs by navArgs()

        val detailedModelFactory =
            DetailedViewModelFactory(args.strangerId, requireActivity().application)

        viewModel = ViewModelProvider(this, detailedModelFactory)[DetailedViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.strangerDetails.observe(viewLifecycleOwner) { strangerPerson ->

            bindImage(binding.ivDetailedPicture, strangerPerson.pictureUrl)
            requestLocationCoordinates(strangerPerson.houseCity)
        }

        locationUtil = LocationUtil.getInstance((activity as (MainActivity)))





        viewModel.address.observe(viewLifecycleOwner) {
            Toast.makeText(this.activity, "Getting Location", Toast.LENGTH_SHORT).show()
            viewModel.getWeather(it)
        }

        viewModel.weather.observe(viewLifecycleOwner) {
            binding.tvLocationDetailed.text = it.cityName
            binding.tvWeatherAqiDetailed.text = it.aqi.toString()
            val temp = "${it.temp.toInt()} °C"
            binding.tvWeatherTempDetailed.text = temp
            binding.tvWeatherDescDetailed.text = it.weatherDescription
            bindImage(binding.ivWeatherDetailedImg, getWeatherImgUrl(it.weatherIcon))
        }
        return binding.root
    }


    fun requestLocationCoordinates(citName: String) {
        val address = locationUtil.geoCoderConverter(citName)
        viewModel.address.postValue(address)
        Toast.makeText(activity, "Activated Request", Toast.LENGTH_SHORT).show()
    }

    fun getWeatherImgUrl(iconCode: String): String {
        val icon = iconCode ?: "r01d"
        return "https://www.weatherbit.io/static/img/icons/${icon}.png"
    }
}
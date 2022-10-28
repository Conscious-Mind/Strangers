package com.davidson.strangers.viewmodels

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.davidson.strangers.database.getDatabase
import com.davidson.strangers.repository.StrangerRepository
import com.davidson.strangers.util.Constants.Companion.API_WEATHER_KEY_2
import com.davidson.strangers.util.Constants.Companion.TIRED_TAG
import kotlinx.coroutines.launch



class OverviewViewModel(application: Application) : AndroidViewModel(application) {


    private val strangerRepository = StrangerRepository(getDatabase(application))

    val strangerList = strangerRepository.strangers

    val weather = strangerRepository.weatherHome

//    val address = MutableLiveData<Address>()

    val location = MutableLiveData<Location>()

    private var weatherLoaded = false


    init {
        viewModelScope.launch {
            refreshRepositoryUsingNetwork()
        }
    }


    private suspend fun refreshRepositoryUsingNetwork() {
        try {
            strangerRepository.refreshStrangerDb()
        } catch (e: Exception) {
            Log.d(TIRED_TAG, "${e.message}")
        }
    }

    fun getWeather(location: Location) {
        viewModelScope.launch {
            strangerRepository.getWeatherDetailsFromNetworkForHome(
                location.latitude,
                location.longitude,
                API_WEATHER_KEY_2
            )
        }
    }

    fun searchInStrangersList(strangerName: String) {
        strangerRepository.searchInDb(strangerName)
    }

    fun isWeatherLoaded(): Boolean = weatherLoaded

    fun weatherLoaded() {
        weatherLoaded = true
    }
}
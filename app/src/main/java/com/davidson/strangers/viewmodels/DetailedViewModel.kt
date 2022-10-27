package com.davidson.strangers.viewmodels

import android.app.Application
import android.location.Address
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.davidson.strangers.database.getDatabase
import com.davidson.strangers.domain.StrangerPerson
import com.davidson.strangers.repository.StrangerRepository
import com.davidson.strangers.util.Constants
import kotlinx.coroutines.launch

class DetailedViewModel(strangerId: Long, app: Application) : AndroidViewModel(app) {

    var strangerDetails = MutableLiveData<StrangerPerson>()

    private val database = getDatabase(app)
    private val repository = StrangerRepository(database)

    val address = MutableLiveData<Address>()

    val weather = repository.weatherDetailed

    init {
        viewModelScope.launch {
            getStrangerPersonById(strangerId)
        }
    }

    private suspend fun getStrangerPersonById(strangerId: Long) {
        val strangerPerson = repository.getStrangerDetailsFromDb(strangerId)
        strangerDetails.postValue(strangerPerson)
    }

    fun getWeather(address: Address) {
        viewModelScope.launch {
            repository.getWeatherDetailsFromNetworkForDetailed(
                address.latitude, address.longitude,
                Constants.API_WEATHER_KEY_2
            )
        }
    }
}
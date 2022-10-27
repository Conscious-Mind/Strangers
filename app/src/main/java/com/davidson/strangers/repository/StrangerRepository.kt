package com.davidson.strangers.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.davidson.strangers.database.DatabaseStranger
import com.davidson.strangers.database.StrangerDatabase
import com.davidson.strangers.database.asDomainModel
import com.davidson.strangers.domain.StrangerPerson
import com.davidson.strangers.domain.WeatherModel
import com.davidson.strangers.network.RandomUserNetwork
import com.davidson.strangers.network.WeatherNetwork
import com.davidson.strangers.test.asDatabaseModel
import com.davidson.strangers.test.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StrangerRepository(private val database: StrangerDatabase) {

    private val _strangers = MutableLiveData<List<DatabaseStranger>>()
    val strangers: LiveData<List<StrangerPerson>>
        get() = Transformations.map(_strangers) {
            it.asDomainModel()
        }

    private val _weatherHome = MutableLiveData<WeatherModel>()
    val weatherHome: LiveData<WeatherModel>
        get() = _weatherHome

    private val _weatherDetailed = MutableLiveData<WeatherModel>()
    val weatherDetailed: LiveData<WeatherModel>
        get() = _weatherDetailed


    init {
        refreshStrangerListInRepo()
    }


    fun refreshStrangerListInRepo() {
        CoroutineScope(Dispatchers.IO).launch {
            _strangers.postValue(database.strangerDao.getAllStrangerFromDb())
        }
    }

    fun searchInDb(searchName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (searchName == "") {
                _strangers.postValue(database.strangerDao.getAllStrangerFromDb())
            } else {
                _strangers.postValue(database.strangerDao.search(searchName))
            }

        }
    }


    suspend fun refreshStrangerDb() {
        withContext(Dispatchers.IO) {
            try {
                val randomUserContainerFromNetwork =
                    RandomUserNetwork.retrofitService.getAllRandomUserFromNetwork()

                database.strangerDao.deleteAll()
                database.strangerDao.resetAutoInc()

//                val listOfRandomUser = randomUserContainerFromNetwork.results
                database.strangerDao.insertAll(randomUserContainerFromNetwork.asDatabaseModel())
                refreshStrangerListInRepo()

            } catch (e: Exception) {
                Log.e("ERROR_REPO", (e.message.toString() ?: "ERROR IN REPO"))
            }
        }
    }

    suspend fun getStrangerDetailsFromDb(strangerId: Long): StrangerPerson {
        return withContext(Dispatchers.IO) {
            database.strangerDao.getStrangerFromDb(strangerId).asDomainModel()
        }
    }

    suspend fun getWeatherDetailsFromNetworkForHome(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                Log.i("Repo", "Calling Weather Service")

                val weatherDetailsFromNetwork =
                    WeatherNetwork.retrofitWeatherService.getWeatherFromNetwork(
                        latitude,
                        longitude,
                        apiKey
                    )

                Log.i("Repo", "Got Weather Service")

                this@StrangerRepository._weatherHome.postValue(weatherDetailsFromNetwork.asDomainModel())


            } catch (e: Exception) {
                Log.e("Repo", "${e.message}")
            }
        }
    }

    suspend fun getWeatherDetailsFromNetworkForDetailed(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                Log.i("Repo", "Calling Weather Service")

                val weatherDetailsFromNetwork =
                    WeatherNetwork.retrofitWeatherService.getWeatherFromNetwork(
                        latitude,
                        longitude,
                        apiKey
                    )

                Log.i("Repo", "Got Weather Service")

                this@StrangerRepository._weatherDetailed.postValue(weatherDetailsFromNetwork.asDomainModel())


            } catch (e: Exception) {
                Log.e("Repo", "${e.message}")
            }
        }
    }
}
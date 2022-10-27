package com.davidson.strangers.domain

data class StrangerPerson(
    val name: String,
    val age: String,
    val location: String,
    val id: Long = 0,
    val pictureUrl: String,
    val gender: String,
    val houseNo: String,
    val houseStreet: String,
    val houseCity: String,
    val state: String,
    val country: String,
    val postcode: String,
    val phone: String,
    val email: String,
    val cell: String,
    val latitude: String,
    val longitude: String
)

data class WeatherModel(
    val temp: Double,
    val aqi: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val cityName: String
)
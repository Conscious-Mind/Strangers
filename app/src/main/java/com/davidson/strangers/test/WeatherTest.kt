package com.davidson.strangers.test


import com.davidson.strangers.domain.WeatherModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherTest(
    @Json(name = "data")
    val `data`: List<Data>,
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "app_temp")
        val appTemp: Double, // 24.25
        @Json(name = "aqi")
        val aqi: Int, // 45
        @Json(name = "city_name")
        val cityName: String, // Raleigh
        @Json(name = "clouds")
        val clouds: Double, // 75
//        @Json(name = "country_code")
//        val countryCode: String, // US
//        @Json(name = "datetime")
//        val datetime: String, // 2017-08-28:17
//        @Json(name = "h_angle")
//        val hAngle: Double, // 0
        @Json(name = "lat")
        val lat: Double, // 35.7721
        @Json(name = "lon")
        val lon: Double, // -78.63861
//        @Json(name = "ob_time")
//        val obTime: String, // 2017-08-28 16:45
        @Json(name = "temp")
        val temp: Double, // 24.19
        @Json(name = "timezone")
        val timezone: String, // America/New_York
        @Json(name = "weather")
        val weather: Weather,
//        @Json(name = "wind_dir")
//        val windDir: Double, // 50
//        @Json(name = "wind_spd")
//        val windSpd: Double // 6.17
    ) {
        @JsonClass(generateAdapter = true)
        data class Weather(
            @Json(name = "code")
            val code: Int, // 803
            @Json(name = "description")
            val description: String, // Broken clouds
            @Json(name = "icon")
            val icon: String // c03d
        )
    }
}

fun WeatherTest.asDomainModel(): WeatherModel {
    val data = this.data[0]
    return WeatherModel(
        temp = data.temp,
        aqi = data.aqi,
        weatherDescription = data.weather.description,
        weatherIcon = data.weather.icon,
        cityName = data.cityName
    )
}


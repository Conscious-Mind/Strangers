package com.davidson.strangers.test


import com.davidson.strangers.database.DatabaseStranger
import com.davidson.strangers.domain.StrangerPerson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TestCase(
    @Json(name = "info")
    val info: Info,
    @Json(name = "results")
    val results: List<Result>
) {
    @JsonClass(generateAdapter = true)
    data class Info(
        @Json(name = "page")
        val page: Int, // 1
        @Json(name = "results")
        val results: Int, // 20
        @Json(name = "seed")
        val seed: String, // 0d11f546528f796b
        @Json(name = "version")
        val version: String // 1.4
    )

    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "cell")
        val cell: String, // 92390291
        @Json(name = "dob")
        val dob: Dob,
        @Json(name = "email")
        val email: String, // benedicte.harstad@example.com
        @Json(name = "gender")
        val gender: String, // female
        @Json(name = "id")
        val id: Id,
        @Json(name = "location")
        val location: Location,
        @Json(name = "login")
        val login: Login,
        @Json(name = "name")
        val name: Name,
        @Json(name = "nat")
        val nat: String, // NO
        @Json(name = "phone")
        val phone: String, // 25909474
        @Json(name = "picture")
        val picture: Picture,
        @Json(name = "registered")
        val registered: Registered
    ) {
        @JsonClass(generateAdapter = true)
        data class Dob(
            @Json(name = "age")
            val age: Int, // 21
            @Json(name = "date")
            val date: String // 2000-12-21T23:10:15.766Z
        )

        @JsonClass(generateAdapter = true)
        data class Id(
            @Json(name = "name")
            val name: String, // FN
            @Json(name = "value")
            val value: String? // 21120083845
        )

        @JsonClass(generateAdapter = true)
        data class Location(
            @Json(name = "city")
            val city: String, // Norderhov
            @Json(name = "coordinates")
            val coordinates: Coordinates,
            @Json(name = "country")
            val country: String, // Norway
            @Json(name = "postcode")
            val postcode: String, // 93452
            @Json(name = "state")
            val state: String, // Sogn og Fjordane
            @Json(name = "street")
            val street: Street,
            @Json(name = "timezone")
            val timezone: Timezone
        ) {
            @JsonClass(generateAdapter = true)
            data class Coordinates(
                @Json(name = "latitude")
                val latitude: String, // 41.3039
                @Json(name = "longitude")
                val longitude: String // 47.2542
            )

            @JsonClass(generateAdapter = true)
            data class Street(
                @Json(name = "name")
                val name: String, // Skogbakken
                @Json(name = "number")
                val number: Int // 9023
            )

            @JsonClass(generateAdapter = true)
            data class Timezone(
                @Json(name = "description")
                val description: String, // Azores, Cape Verde Islands
                @Json(name = "offset")
                val offset: String // -1:00
            )
        }

        @JsonClass(generateAdapter = true)
        data class Login(
            @Json(name = "md5")
            val md5: String, // 1207159740a382b8c963d8c478734095
            @Json(name = "password")
            val password: String, // alfa
            @Json(name = "salt")
            val salt: String, // YiNPnEEj
            @Json(name = "sha1")
            val sha1: String, // 18db5aeca982f9bb8deb56976d4eb7846e15d374
            @Json(name = "sha256")
            val sha256: String, // a00f19fc579d27d40240af993b20b4fa976bbe083732555b631d1e318cad78d4
            @Json(name = "username")
            val username: String, // heavypanda215
            @Json(name = "uuid")
            val uuid: String // 26e6ec9b-5c2a-4a37-b217-b2fa318f0f6b
        )

        @JsonClass(generateAdapter = true)
        data class Name(
            @Json(name = "first")
            val first: String, // Benedicte
            @Json(name = "last")
            val last: String, // Hårstad
            @Json(name = "title")
            val title: String // Ms
        )

        @JsonClass(generateAdapter = true)
        data class Picture(
            @Json(name = "large")
            val large: String, // https://randomuser.me/api/portraits/women/23.jpg
            @Json(name = "medium")
            val medium: String, // https://randomuser.me/api/portraits/med/women/23.jpg
            @Json(name = "thumbnail")
            val thumbnail: String // https://randomuser.me/api/portraits/thumb/women/23.jpg
        )

        @JsonClass(generateAdapter = true)
        data class Registered(
            @Json(name = "age")
            val age: Int, // 7
            @Json(name = "date")
            val date: String // 2015-06-09T00:14:21.719Z
        )
    }
}


fun TestCase.asDomainModel(): List<StrangerPerson> {
    return results.map {
        StrangerPerson(
            name = "${it.name.first} ${it.name.last}",
            age = it.dob.age.toString(),
            location = it.location.country,
            pictureUrl = it.picture.large,
            gender = it.gender,
            houseNo = it.location.street.number.toString(),
            houseStreet = it.location.street.name,
            state = it.location.state,
            country = it.location.country,
            postcode = it.location.postcode,
            cell = it.cell,
            phone = it.phone,
            email = it.email,
            houseCity = it.location.city,
            latitude = it.location.coordinates.latitude,
            longitude = it.location.coordinates.longitude
        )
    }
}

fun TestCase.asDatabaseModel(): List<DatabaseStranger> {
    return results.map {
        DatabaseStranger(
            strangerName = "${it.name.first} ${it.name.last}",
            strangerAge = it.dob.age.toString(),
            strangerLocation = it.location.country,
            strangerImageUrl = it.picture.large,
            strangerGender = it.gender,
            strangerHouseNo = it.location.street.number.toString(),
            strangerHouseStreet = it.location.street.name,
            strangerHouseCity = it.location.city,
            strangerState = it.location.state,
            strangerCountry = it.location.country,
            strangerPostcode = it.location.postcode,
            strangerCell = it.cell,
            strangerPhone = it.phone,
            strangerEmail = it.email,
            strangerLongitude = it.location.coordinates.longitude,
            strangerLatitude = it.location.coordinates.latitude
        )
    }
}
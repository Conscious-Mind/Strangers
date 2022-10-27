package com.davidson.strangers.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidson.strangers.domain.StrangerPerson

@Entity(tableName = "databaseStranger")
data class DatabaseStranger constructor(
    @PrimaryKey(autoGenerate = true)
    val strangerId: Long = 0,
    val strangerName: String,
    val strangerAge: String,
    val strangerLocation: String,
    val strangerImageUrl: String,
    val strangerGender: String,
    val strangerHouseNo: String,
    val strangerHouseStreet: String,
    val strangerHouseCity: String,
    val strangerState: String,
    val strangerCountry: String,
    val strangerPostcode: String,
    val strangerPhone: String,
    val strangerEmail: String,
    val strangerCell: String,
    val strangerLatitude: String,
    val strangerLongitude: String,
)

fun List<DatabaseStranger>.asDomainModel(): List<StrangerPerson> {
    return map {
        StrangerPerson(
            id = it.strangerId,
            name = it.strangerName,
            age = it.strangerAge,
            location = it.strangerLocation,
            pictureUrl = it.strangerImageUrl,
            gender = it.strangerGender,
            houseNo = it.strangerHouseNo,
            houseStreet = it.strangerHouseStreet,
            state = it.strangerState,
            country = it.strangerCountry,
            postcode = it.strangerPostcode,
            cell = it.strangerCell,
            phone = it.strangerPhone,
            email = it.strangerEmail,
            houseCity = it.strangerHouseCity,
            latitude = it.strangerLatitude,
            longitude = it.strangerLongitude
        )
    }
}

fun DatabaseStranger.asDomainModel(): StrangerPerson {
    return StrangerPerson(
        id = strangerId,
        name = strangerName,
        age = strangerAge,
        location = strangerLocation,
        pictureUrl = strangerImageUrl,
        gender = strangerGender,
        houseNo = strangerHouseNo,
        houseStreet = strangerHouseStreet,
        state = strangerState,
        country = strangerCountry,
        postcode = strangerPostcode,
        cell = strangerCell,
        phone = strangerPhone,
        email = strangerEmail,
        houseCity = strangerHouseCity,
        latitude = strangerLatitude,
        longitude = strangerLongitude
    )
}
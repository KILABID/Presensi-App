package com.murad.presensi.data.local.model

data class AttendanceModel(
    val id: String = "", // Default empty id
    val location: Location = Location(), // Default Location
    val status: String = "Unknown", // Default status
    val time: Time = Time(), // Default Time
    val date: String = "" // Default empty date
)

data class Location(
    val latitude: Double = 0.0, // Default latitude
    val longitude: Double = 0.0 // Default longitude
)

data class Time(
    val checkIn: String? = "00:00", // Default check-in time
    val checkOut: String? = "00:00" // Default check-out time
)

data class UserAttendance(
    val id: String = "", // Default empty id
    val name: String = "",
    val date: String = "", // Default empty date
    val history: AttendanceModel = AttendanceModel() // Default empty history
)

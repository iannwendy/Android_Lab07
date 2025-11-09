package com.example.lab07_exercise1.data

data class Contact(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}


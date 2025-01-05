package com.example.becure.data.entities

data class AppointmentWithDetails(
    val appointment: Appointment,
    val doctor: Doctor? = null,
    val patient: Patient? = null
)
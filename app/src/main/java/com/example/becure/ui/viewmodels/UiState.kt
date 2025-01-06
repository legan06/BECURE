package com.example.becure.ui.viewmodels

import com.example.becure.data.entities.Appointment
import com.example.becure.data.entities.Doctor
import com.example.becure.data.entities.Patient

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class DoctorUiState(
    val doctors: UiState<List<Doctor>> = UiState.Loading,
    val selectedDoctor: Doctor? = null,
    val specialtyDoctors: List<Doctor> = emptyList(),
    val error: String? = null
)

data class PatientUiState(
    val patients: UiState<List<Patient>> = UiState.Loading,
    val selectedPatient: Patient? = null,
    val error: String? = null
)

data class AppointmentUiState(
    val appointments: UiState<List<Appointment>> = UiState.Loading,
    val selectedAppointment: Appointment? = null,
    val doctorAppointments: List<Appointment> = emptyList(),
    val patientAppointments: List<Appointment> = emptyList(),
    val error: String? = null
)
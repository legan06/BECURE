package com.example.becure.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.becure.data.entities.Appointment
import com.example.becure.data.entities.AppointmentWithDetails
import com.example.becure.repository.AppointmentRepository
import com.example.becure.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentUiState())
    val uiState: StateFlow<AppointmentUiState> = _uiState

    private val _detailedAppointments = MutableStateFlow<List<AppointmentWithDetails>>(emptyList())
    val detailedAppointments: StateFlow<List<AppointmentWithDetails>> = _detailedAppointments

    init {
        loadAppointments()
        refreshAppointmentsFromApi()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            appointmentRepository.getAllAppointmentsFromDb()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        appointments = UiState.Error(e.message ?: "Unknown error occurred")
                    )
                }
                .collect { appointments ->
                    _uiState.value = _uiState.value.copy(
                        appointments = UiState.Success(appointments)
                    )
                    loadAppointmentDetails(appointments)
                }
        }
    }

    private suspend fun loadAppointmentDetails(appointments: List<Appointment>) {
        try {
            val detailedList = appointments.map { appointment ->
                val doctor = doctorRepository.getDoctorByIdFromDb(appointment.doctorId)
                AppointmentWithDetails(
                    appointment = appointment,
                    doctor = doctor
                )
            }
            _detailedAppointments.value = detailedList
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Failed to load appointment details"
            )
        }
    }

    fun refreshAppointmentsFromApi() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(appointments = UiState.Loading)
                appointmentRepository.refreshAppointments()
                // After refreshing appointments, load them again
                loadAppointments()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to refresh appointments"
                )
            }
        }
    }

    fun loadDoctorAppointments(doctorId: Long) {
        viewModelScope.launch {
            try {
                val appointments = appointmentRepository.getDoctorAppointments(doctorId)
                _uiState.value = _uiState.value.copy(
                    doctorAppointments = appointments
                )
                loadAppointmentDetails(appointments)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load doctor appointments"
                )
            }
        }
    }

    fun loadPatientAppointments(patientId: Long) {
        viewModelScope.launch {
            try {
                val appointments = appointmentRepository.getPatientAppointments(patientId)
                val detailedAppointments = appointments.map { appointment ->
                    val doctor = doctorRepository.getDoctorByIdFromDb(appointment.doctorId)
                    AppointmentWithDetails(
                        appointment = appointment,
                        doctor = doctor
                    )
                }
                _detailedAppointments.value = detailedAppointments
                _uiState.value = _uiState.value.copy(
                    patientAppointments = appointments
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load patient appointments"
                )
            }
        }
    }

    fun createAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                appointmentRepository.insertAppointment(appointment)
                refreshAppointmentsFromApi()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to create appointment"
                )
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                appointmentRepository.updateAppointment(appointment)
                refreshAppointmentsFromApi()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update appointment"
                )
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                appointmentRepository.deleteAppointment(appointment)
                refreshAppointmentsFromApi()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete appointment"
                )
            }
        }
    }

    class Factory(
        private val appointmentRepository: AppointmentRepository,
        private val doctorRepository: DoctorRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppointmentViewModel(appointmentRepository, doctorRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
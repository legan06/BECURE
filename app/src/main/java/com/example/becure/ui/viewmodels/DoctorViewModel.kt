package com.example.becure.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.becure.data.entities.Doctor
import com.example.becure.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DoctorViewModel(
    private val repository: DoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorUiState())
    val uiState: StateFlow<DoctorUiState> = _uiState

    init {
        loadDoctors()
        refreshDoctorsFromApi()
    }

    private fun loadDoctors() {
        viewModelScope.launch {
            repository.getAllDoctorsFromDb()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        doctors = UiState.Error(e.message ?: "Unknown error occurred")
                    )
                }
                .collect { doctors ->
                    _uiState.value = _uiState.value.copy(
                        doctors = UiState.Success(doctors)
                    )
                }
        }
    }

    fun refreshDoctorsFromApi() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(doctors = UiState.Loading)
                repository.refreshDoctors()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to refresh doctors"
                )
            }
        }
    }

    fun getDoctorsBySpecialty(specialty: String) {
        viewModelScope.launch {
            try {
                val doctors = repository.getDoctorsBySpecialtyFromDb(specialty)
                _uiState.value = _uiState.value.copy(
                    specialtyDoctors = doctors
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to get doctors by specialty"
                )
            }
        }
    }

    fun selectDoctor(doctorId: Long) {
        viewModelScope.launch {
            try {
                val doctor = repository.getDoctorByIdFromDb(doctorId)
                _uiState.value = _uiState.value.copy(
                    selectedDoctor = doctor
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to select doctor"
                )
            }
        }
    }

    class Factory(private val repository: DoctorRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DoctorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
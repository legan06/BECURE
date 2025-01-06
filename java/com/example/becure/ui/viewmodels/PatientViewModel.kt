package com.example.becure.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.becure.data.entities.Patient
import com.example.becure.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {
    private val TAG = "PatientViewModel"
    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> = _uiState

    init {
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            repository.getAllPatientsFromDb()
                .catch { e ->
                    Log.e(TAG, "Error loading patients: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(
                        patients = UiState.Error(e.message ?: "Unknown error occurred")
                    )
                }
                .collect { patients ->
                    Log.d(TAG, "Loaded ${patients.size} patients from database")
                    _uiState.value = _uiState.value.copy(
                        patients = UiState.Success(patients)
                    )
                }
        }
    }

    fun refreshPatientsFromApi() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Refreshing patients from API")
                _uiState.value = _uiState.value.copy(patients = UiState.Loading)
                repository.refreshPatients()
                // After refresh, loadPatients will automatically update the UI state
                loadPatients()
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing patients: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to refresh patients"
                )
            }
        }
    }

    fun selectPatient(patientId: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Selecting patient with ID: $patientId")
                val patient = repository.getPatientByIdFromDb(patientId)
                if (patient != null) {
                    Log.d(TAG, "Patient found: ${patient.name}")
                    _uiState.value = _uiState.value.copy(
                        selectedPatient = patient
                    )
                } else {
                    Log.e(TAG, "No patient found with ID: $patientId")
                    _uiState.value = _uiState.value.copy(
                        error = "Patient not found"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error selecting patient: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to select patient"
                )
            }
        }
    }

    class Factory(private val repository: PatientRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PatientViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
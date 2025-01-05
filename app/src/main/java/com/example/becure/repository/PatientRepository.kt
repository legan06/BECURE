package com.example.becure.repository

import android.content.Context
import android.util.Log
import com.example.becure.data.database.BeCureDatabase
import com.example.becure.data.entities.Patient
import com.example.becure.network.api.RetrofitClient
import kotlinx.coroutines.flow.Flow

class PatientRepository(context: Context) {
    private val TAG = "PatientRepository"
    private val database = BeCureDatabase.getDatabase(context)
    private val patientDao = database.patientDao()
    private val apiService = RetrofitClient.apiService

    // Local database operations
    fun getAllPatientsFromDb(): Flow<List<Patient>> {
        return patientDao.getAllPatients()
    }

    suspend fun getPatientByIdFromDb(id: Long): Patient? {
        Log.d(TAG, "Getting patient by ID: $id")
        val patient = patientDao.getPatientById(id)
        if (patient != null) {
            Log.d(TAG, "Found patient: ${patient.name}")
        } else {
            Log.d(TAG, "No patient found with ID: $id")
        }
        return patient
    }

    // API operations with local caching
    suspend fun refreshPatients() {
        try {
            Log.d(TAG, "Refreshing patients from API")
            val patients = apiService.getAllPatients()
            Log.d(TAG, "Received ${patients.size} patients from API")

            // Clear existing data
            patientDao.deleteAllPatients()

            // Insert new data
            patientDao.insertAllPatients(patients)
            Log.d(TAG, "Saved patients to local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing patients: ${e.message}", e)
            throw e
        }
    }

    // Local database operations for single patient
    suspend fun insertPatient(patient: Patient): Long {
        Log.d(TAG, "Inserting patient: ${patient.name}")
        return patientDao.insertPatient(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        Log.d(TAG, "Deleting patient: ${patient.name}")
        patientDao.deletePatient(patient)
    }
}
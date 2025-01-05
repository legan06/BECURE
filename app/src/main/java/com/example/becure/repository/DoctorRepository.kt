package com.example.becure.repository

import android.content.Context
import com.example.becure.data.database.BeCureDatabase
import com.example.becure.data.entities.Doctor
import com.example.becure.network.api.RetrofitClient
import com.example.becure.network.models.DoctorResponse
import kotlinx.coroutines.flow.Flow

class DoctorRepository(context: Context) {
    private val database = BeCureDatabase.getDatabase(context)
    private val doctorDao = database.doctorDao()
    private val apiService = RetrofitClient.apiService

    private fun DoctorResponse.toDoctor(): Doctor {
        return Doctor(
            doctorId = this.doctorId,
            name = this.name,
            specialty = this.specialty,
            phoneNumber = this.phoneNumber,
            email = this.email,
            profileImage = this.profileImage
        )
    }

    // Local database operations
    fun getAllDoctorsFromDb(): Flow<List<Doctor>> {
        return doctorDao.getAllDoctors()
    }

    suspend fun getDoctorByIdFromDb(id: Long): Doctor? {
        return doctorDao.getDoctorById(id)
    }

    suspend fun getDoctorsBySpecialtyFromDb(specialty: String): List<Doctor> {
        return doctorDao.getDoctorsBySpecialty(specialty)
    }

    // API operations with local caching
    suspend fun refreshDoctors() {
        try {
            val response = apiService.getAllDoctors()
            response.doctors?.let { doctorResponses ->
                doctorDao.deleteAllDoctors() // Clear existing data
                // Convert DoctorResponse to Doctor entities
                val doctors = doctorResponses.map { it.toDoctor() }
                doctorDao.insertAllDoctors(doctors)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // Local database operations
    suspend fun insertDoctor(doctor: Doctor): Long {
        return doctorDao.insertDoctor(doctor)
    }

    suspend fun deleteDoctor(doctor: Doctor) {
        doctorDao.deleteDoctor(doctor)
    }
}
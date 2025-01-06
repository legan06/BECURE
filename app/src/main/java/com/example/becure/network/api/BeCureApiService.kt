package com.example.becure.network.api

import com.example.becure.data.entities.Appointment
import com.example.becure.data.entities.Patient
import com.example.becure.network.models.DoctorResponse
import com.example.becure.network.models.DoctorsWrapper
import retrofit2.http.*

interface BeCureApiService {
    // Doctors endpoints
    @GET("A3OB")  // Your doctors endpoint
    suspend fun getAllDoctors(): DoctorsWrapper

    // Patients endpoints
    @GET("INQ8")  // Your patients endpoint
    suspend fun getAllPatients(): List<Patient>

    // Appointments endpoints
    @GET("Z09F")  // Your appointments endpoint
    suspend fun getAllAppointments(): List<Appointment>
}
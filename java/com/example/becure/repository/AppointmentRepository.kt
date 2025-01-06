package com.example.becure.repository

import android.content.Context
import com.example.becure.data.database.BeCureDatabase
import com.example.becure.data.entities.Appointment
import com.example.becure.network.api.RetrofitClient
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(context: Context) {
    private val database = BeCureDatabase.getDatabase(context)
    private val appointmentDao = database.appointmentDao()
    private val apiService = RetrofitClient.apiService

    // Local database operations
    fun getAllAppointmentsFromDb(): Flow<List<Appointment>> {
        return appointmentDao.getAllAppointments()
    }

    suspend fun getAppointmentByIdFromDb(id: Long): Appointment? {
        return appointmentDao.getAppointmentById(id)
    }

    suspend fun getPatientAppointments(patientId: Long): List<Appointment> {
        return appointmentDao.getAppointmentsByPatientId(patientId)
    }

    suspend fun getDoctorAppointments(doctorId: Long): List<Appointment> {
        return appointmentDao.getAppointmentsByDoctorId(doctorId)
    }

    // API operations with local caching
    suspend fun refreshAppointments() {
        try {
            val appointments = apiService.getAllAppointments()
            appointmentDao.insertAllAppointments(appointments)
        } catch (e: Exception) {
            throw e
        }
    }

    // Local database operations
    suspend fun insertAppointment(appointment: Appointment) {
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        appointmentDao.deleteAppointment(appointment)
    }
}
package com.example.becure.data.dao

import androidx.room.*
import com.example.becure.data.entities.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId")
    suspend fun getAppointmentsByPatientId(patientId: Long): List<Appointment>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId")
    suspend fun getAppointmentsByDoctorId(doctorId: Long): List<Appointment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAppointments(appointments: List<Appointment>)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("SELECT * FROM appointments WHERE appointmentId = :id")
    suspend fun getAppointmentById(id: Long): Appointment?
}
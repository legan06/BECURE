package com.example.becure.data.dao

import androidx.room.*
import com.example.becure.data.entities.Doctor
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors")
    fun getAllDoctors(): Flow<List<Doctor>>

    @Query("SELECT * FROM doctors WHERE doctorId = :id")
    suspend fun getDoctorById(id: Long): Doctor?

    @Query("SELECT * FROM doctors WHERE specialty = :specialty")
    suspend fun getDoctorsBySpecialty(specialty: String): List<Doctor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: Doctor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDoctors(doctors: List<Doctor>)

    @Delete
    suspend fun deleteDoctor(doctor: Doctor)

    @Query("DELETE FROM doctors")
    suspend fun deleteAllDoctors()
}
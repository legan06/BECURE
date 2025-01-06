package com.example.becure.data.dao

import androidx.room.*
import com.example.becure.data.entities.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun getPatientById(id: Long): Patient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPatients(patients: List<Patient>)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()
}
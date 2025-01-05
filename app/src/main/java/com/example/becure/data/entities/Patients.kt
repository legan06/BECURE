package com.example.becure.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val patientId: Long = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: String
)
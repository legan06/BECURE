package com.example.becure.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true)
    val doctorId: Long,
    val name: String,
    val specialty: String,
    val phoneNumber: String,
    val email: String,
    val profileImage: String?
)
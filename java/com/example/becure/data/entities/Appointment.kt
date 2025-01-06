package com.example.becure.data.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = Doctor::class,
            parentColumns = ["doctorId"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val appointmentId: Long = 0,
    val doctorId: Long,
    val patientId: Long,
    val dateTime: String,
    val status: String, // SCHEDULED, COMPLETED, CANCELLED
    val notes: String?,
    val isPatient: Boolean
)
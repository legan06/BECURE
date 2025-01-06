package com.example.becure.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.becure.data.dao.AppointmentDao
import com.example.becure.data.dao.DoctorDao
import com.example.becure.data.dao.PatientDao
import com.example.becure.data.entities.Appointment
import com.example.becure.data.entities.Doctor
import com.example.becure.data.entities.Patient

@Database(
    entities = [Doctor::class, Patient::class, Appointment::class],
    version = 1,
    exportSchema = false
)
abstract class BeCureDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: BeCureDatabase? = null

        fun getDatabase(context: Context): BeCureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BeCureDatabase::class.java,
                    "becure_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
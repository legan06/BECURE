package com.example.becure

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.becure.databinding.ActivityPatientSelectionBinding

class PatientSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_patient_selection.xml ile bağdaştırma
        binding = ActivityPatientSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cardCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }

        binding.cardMyAppointments.setOnClickListener {
            val intent = Intent(this, PatientAppointmentsActivity::class.java)
            startActivity(intent)
        }
    }
}

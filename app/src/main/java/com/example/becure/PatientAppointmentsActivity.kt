package com.example.becure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.becure.databinding.ActivityPatientAppointmentsBinding

class PatientAppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding ile layout'u bağla
        binding = ActivityPatientAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}

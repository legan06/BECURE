package com.example.becure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.becure.databinding.ActivityDoctorAppointmentsBinding

class DoctorAppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_doctor_appointments.xml layout dosyasını bağla
        binding = ActivityDoctorAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}

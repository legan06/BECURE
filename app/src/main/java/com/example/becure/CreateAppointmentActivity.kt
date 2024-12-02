package com.example.becure

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.becure.databinding.ActivityCreateAppointmentBinding

class CreateAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding ile layout'u bağla
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}

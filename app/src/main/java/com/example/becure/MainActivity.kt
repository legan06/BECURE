package com.example.becure

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.becure.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var  mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        // Apply WindowInsets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Doctor card'a tıkladığında Doctor verisi ile beraber login aktivitesini aç
        mainBinding.cardDoctor.setOnClickListener {
            navigateToLogin("Doctor")
        }

        // Doctor card'a tıkladığında patient verisi ile beraber login aktivitesini aç
        mainBinding.cardPatient.setOnClickListener {
            navigateToLogin("Patient")
        }

    }

    private fun navigateToLogin(userType: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("USER_TYPE", userType)
        }
        startActivity(intent)
    }

}

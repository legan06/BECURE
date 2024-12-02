package com.example.becure

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.becure.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        // Apply WindowInsets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(loginBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Kullanıcı türünü al
        val userType = intent.getStringExtra("USER_TYPE") ?: "Unknown"

        // Login butonuna tıklandığında uygun aktiviteyi başlat
        loginBinding.buttonLogin.setOnClickListener {
            if (userType == "Doctor") {
                // Doctor için DoctorAppointmentsActivity başlat
                val intent = Intent(this, DoctorAppointmentsActivity::class.java)
                startActivity(intent)
            } else if (userType == "Patient") {
                // Patient için PatientSelectionActivity başlat
                val intent = Intent(this, PatientSelectionActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

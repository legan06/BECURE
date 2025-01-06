package com.example.becure.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.becure.databinding.ActivityMainBinding
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.becure.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupClickListeners()
        checkNotificationPermission()
        scheduleReminderWorker()
        // Müzik servisini başlat
        val intent = Intent(this, BackgroundMusicService::class.java)
        startService(intent)

        //Glide
        // ImageView'e eriş
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Glide ile görsel yükle
        Glide.with(this)
            .load("https://ctis.bilkent.edu.tr/assets/images/ctislogo.png") // Görsel URL
            .placeholder(R.drawable.placeholder) // Yer tutucu görsel
            .into(imageView)

    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun scheduleReminderWorker() {
        // 1 dakikada bir çalışan WorkRequest
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(10, TimeUnit.SECONDS)
            .build()

        // Worker'ı enqueueliyoruz
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
    private fun setupClickListeners() {
        binding.cardDoctor.setOnClickListener {
            navigateToLogin("Doctor")
        }

        binding.cardPatient.setOnClickListener {
            navigateToLogin("Patient")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Müzik servisini durdur
        val intent = Intent(this, BackgroundMusicService::class.java)
        stopService(intent)
    }
    private fun navigateToLogin(userType: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra(LoginActivity.USER_TYPE_KEY, userType)
        }
        startActivity(intent)
    }
}
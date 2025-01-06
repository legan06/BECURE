package com.example.becure.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.becure.databinding.ActivityLoginBinding
import com.example.becure.repository.DoctorRepository
import com.example.becure.repository.PatientRepository
import com.example.becure.ui.viewmodels.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var userType: String = "Unknown"
    private val TAG = "LoginActivity"

    private val doctorViewModel: DoctorViewModel by viewModels {
        DoctorViewModel.Factory(DoctorRepository(this))
    }

    private val patientViewModel: PatientViewModel by viewModels {
        PatientViewModel.Factory(PatientRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra(USER_TYPE_KEY).orEmpty()
        Log.d(TAG, "User type received: $userType")

        setupWindowInsets()
        initializeViews()
        setupObservers()
        refreshData()
    }

    private fun initializeViews() {
        binding.textLoginTitle.text = when (userType) {
            "Doctor" -> "Log In as Doctor"
            "Patient" -> "Log In as Patient"
            else -> {
                userType = "Unknown"
                "Log In"
            }
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.progressBar.visibility == View.VISIBLE) return@setOnClickListener

            val email = binding.username.text.toString()
            if (validateInputs(email)) {
                binding.buttonLogin.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                attemptLogin(email)
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (userType == "Patient") {
                    patientViewModel.uiState.collect { state ->
                        when (state.patients) {
                            is UiState.Loading -> showLoading(true)
                            is UiState.Success -> {
                                showLoading(false)
                                Log.d(TAG, "Patients loaded: ${state.patients.data.size} patients")
                            }
                            is UiState.Error -> {
                                showLoading(false)
                                showError(state.patients.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun attemptLogin(email: String) {
        Log.d(TAG, "Attempting login for $userType with email: $email")
        when (userType) {
            "Doctor" -> attemptDoctorLogin(email)
            "Patient" -> attemptPatientLogin(email)
            else -> {
                showError("Invalid user type")
                showLoading(false)
            }
        }
    }

    private fun attemptPatientLogin(email: String) {
        Log.d(TAG, "Attempting patient login with email: $email")
        when (val state = patientViewModel.uiState.value.patients) {
            is UiState.Success -> {
                val patient = state.data.find { it.email == email }
                if (patient != null) {
                    Log.d(TAG, "Patient found with ID: ${patient.patientId}")
                    navigateToPatient(patient.patientId)
                } else {
                    Log.e(TAG, "No patient found with email: $email")
                    showError("Invalid email or password")
                    showLoading(false)
                }
            }
            is UiState.Loading -> {
                showError("Please wait while loading data")
                showLoading(false)
            }
            is UiState.Error -> {
                showError("Error loading patient data")
                showLoading(false)
            }
        }
    }

    private fun attemptDoctorLogin(email: String) {
        when (val state = doctorViewModel.uiState.value.doctors) {
            is UiState.Success -> {
                val doctor = state.data.find { it.email == email }
                if (doctor != null) {
                    navigateToDoctor(doctor.doctorId)
                } else {
                    showError("Invalid email or password")
                    showLoading(false)
                }
            }
            else -> {
                showError("Please wait while loading data")
                showLoading(false)
            }
        }
    }

    private fun navigateToDoctor(doctorId: Long) {
        val intent = Intent(this, DoctorAppointmentsActivity::class.java).apply {
            putExtra("DOCTOR_ID", doctorId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToPatient(patientId: Long) {
        Log.d(TAG, "Navigating to PatientSelectionActivity with patient ID: $patientId")
        val intent = Intent(this, PatientSelectionActivity::class.java).apply {
            putExtra("PATIENT_ID", patientId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun validateInputs(email: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            binding.usernameLayout.error = "Please enter email"
            isValid = false
        } else {
            binding.usernameLayout.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.usernameLayout.error = "Please enter a valid email"
            isValid = false
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
    }

    private fun refreshData() {
        when (userType) {
            "Doctor" -> doctorViewModel.refreshDoctorsFromApi()
            "Patient" -> patientViewModel.refreshPatientsFromApi()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        const val USER_TYPE_KEY = "user_type"
    }
}
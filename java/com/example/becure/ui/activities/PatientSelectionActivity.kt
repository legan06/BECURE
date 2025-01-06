package com.example.becure.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.becure.databinding.ActivityPatientSelectionBinding
import com.example.becure.repository.PatientRepository
import com.example.becure.ui.viewmodels.PatientViewModel
import com.example.becure.ui.viewmodels.UiState
import kotlinx.coroutines.launch

class PatientSelectionActivity : AppCompatActivity() {
    private val TAG = "PatientSelectionActivity"
    private lateinit var binding: ActivityPatientSelectionBinding
    private var currentPatientId: Long = -1L

    private val patientViewModel: PatientViewModel by viewModels {
        PatientViewModel.Factory(PatientRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get patient ID from intent
        currentPatientId = intent.getLongExtra("PATIENT_ID", -1L)
        Log.d(TAG, "Received patient ID: $currentPatientId")

        if (currentPatientId == -1L) {
            Log.e(TAG, "No patient ID provided")
            showError("No patient ID provided")
            finish()
            return
        }

        setupViews()
        setupObservers()
        loadInitialData()
    }

    private fun setupViews() {
        setupClickListeners()
        // Initially disable buttons until patient is verified
        binding.cardCreateAppointment.isEnabled = false
        binding.cardMyAppointments.isEnabled = false
    }

    private fun setupClickListeners() {
        binding.cardCreateAppointment.setOnClickListener {
            Log.d(TAG, "Navigating to CreateAppointmentActivity with patientId: $currentPatientId")
            val intent = Intent(this, CreateAppointmentActivity::class.java).apply {
                putExtra("PATIENT_ID", currentPatientId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        binding.cardMyAppointments.setOnClickListener {
            val intent = Intent(this, PatientAppointmentsActivity::class.java).apply {
                putExtra(PatientAppointmentsActivity.PATIENT_ID_KEY, currentPatientId)
            }
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                patientViewModel.uiState.collect { state ->
                    Log.d(TAG, "Patient state updated: $state")
                    handlePatientState(state)
                }
            }
        }
    }

    private fun handlePatientState(state: com.example.becure.ui.viewmodels.PatientUiState) {
        // Handle selected patient
        state.selectedPatient?.let { patient ->
            Log.d(TAG, "Selected patient verified: ${patient.name}")
            binding.cardCreateAppointment.isEnabled = true
            binding.cardMyAppointments.isEnabled = true
            binding.progressBar?.visibility = View.GONE
            return
        }

        // Handle patients list state
        when (state.patients) {
            is UiState.Loading -> {
                Log.d(TAG, "Loading patients...")
                binding.progressBar?.visibility = View.VISIBLE
                binding.cardCreateAppointment.isEnabled = false
                binding.cardMyAppointments.isEnabled = false
            }
            is UiState.Success -> {
                Log.d(TAG, "Patients loaded: ${state.patients.data.size}")
                binding.progressBar?.visibility = View.GONE
                val patient = state.patients.data.find { it.patientId == currentPatientId }
                if (patient == null) {
                    Log.e(TAG, "Patient not found in loaded data. ID: $currentPatientId")
                    showError("Invalid patient ID")
                    finish()
                } else {
                    Log.d(TAG, "Patient found: ${patient.name}")
                    binding.cardCreateAppointment.isEnabled = true
                    binding.cardMyAppointments.isEnabled = true
                }
            }
            is UiState.Error -> {
                Log.e(TAG, "Error loading patients: ${state.patients.message}")
                binding.progressBar?.visibility = View.GONE
                binding.cardCreateAppointment.isEnabled = false
                binding.cardMyAppointments.isEnabled = false
                showError(state.patients.message)
            }
        }

        // Handle error state
        state.error?.let { error ->
            Log.e(TAG, "Error state: $error")
            showError(error)
        }
    }

    private fun loadInitialData() {
        Log.d(TAG, "Loading initial data for patient ID: $currentPatientId")
        lifecycleScope.launch {
            try {
                patientViewModel.refreshPatientsFromApi()
                patientViewModel.selectPatient(currentPatientId)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading initial data", e)
                showError("Error loading data: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
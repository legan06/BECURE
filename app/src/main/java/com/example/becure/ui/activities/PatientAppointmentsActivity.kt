package com.example.becure.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.becure.databinding.ActivityPatientAppointmentsBinding
import com.example.becure.repository.AppointmentRepository
import com.example.becure.repository.DoctorRepository
import com.example.becure.ui.adapters.PatientAppointmentsAdapter
import com.example.becure.ui.viewmodels.AppointmentViewModel
import com.example.becure.ui.viewmodels.UiState
import kotlinx.coroutines.launch

class PatientAppointmentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientAppointmentsBinding
    private lateinit var appointmentsAdapter: PatientAppointmentsAdapter
    private var patientId: Long = -1L

    // Updated ViewModel instantiation with both repositories
    private val appointmentViewModel: AppointmentViewModel by viewModels {
        AppointmentViewModel.Factory(
            AppointmentRepository(this),
            DoctorRepository(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get patient ID from intent
        patientId = intent.getLongExtra(PATIENT_ID_KEY, -1L)
        if (!validatePatientId()) {
            return
        }

        initializeUI()
        setupObservers()
        loadInitialData()
    }

    private fun validatePatientId(): Boolean {
        if (patientId == -1L) {
            showError("Invalid patient ID")
            finish()
            return false
        }
        return true
    }

    private fun initializeUI() {
        setupRecyclerView()
        setupRefreshLayout()
    }

    private fun setupRecyclerView() {
        appointmentsAdapter = PatientAppointmentsAdapter()
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(this@PatientAppointmentsActivity)
            adapter = appointmentsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                refreshAppointments()
            }
            setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        }
    }

    private fun setupObservers() {
        // Observe detailed appointments
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentViewModel.detailedAppointments.collect { appointments ->
                    appointmentsAdapter.submitList(appointments)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        // Observe UI state for errors
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentViewModel.uiState.collect { state ->
                    when (state.appointments) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            showError(state.appointments.message)
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                    // Handle any general errors
                    state.error?.let { error ->
                        showError(error)
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        binding.progressBar.visibility = View.VISIBLE
        loadPatientAppointments()
    }

    private fun refreshAppointments() {
        binding.swipeRefreshLayout.isRefreshing = true
        loadPatientAppointments()
    }

    private fun loadPatientAppointments() {
        lifecycleScope.launch {
            try {
                appointmentViewModel.loadPatientAppointments(patientId)
            } catch (e: Exception) {
                showError("Error loading appointments: ${e.message}")
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val PATIENT_ID_KEY = "PATIENT_ID"
    }
}
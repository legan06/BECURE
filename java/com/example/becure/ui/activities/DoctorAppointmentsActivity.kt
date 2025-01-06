package com.example.becure.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.becure.databinding.ActivityDoctorAppointmentsBinding
import com.example.becure.repository.AppointmentRepository
import com.example.becure.repository.DoctorRepository
import com.example.becure.repository.PatientRepository
import com.example.becure.ui.adapters.DoctorAppointmentsAdapter
import com.example.becure.ui.viewmodels.AppointmentViewModel
import kotlinx.coroutines.launch

class DoctorAppointmentsActivity : AppCompatActivity() {
    private val TAG = "DoctorAppointmentsActivity"
    private lateinit var binding: ActivityDoctorAppointmentsBinding
    private lateinit var appointmentsAdapter: DoctorAppointmentsAdapter
    private var currentDoctorId: Long = -1L

    private val appointmentViewModel: AppointmentViewModel by viewModels {
        AppointmentViewModel.Factory(
            AppointmentRepository(this),
            DoctorRepository(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get doctor ID from intent
        currentDoctorId = intent.getLongExtra("DOCTOR_ID", -1L)
        if (currentDoctorId == -1L) {
            showError("No doctor ID provided")
            finish()
            return
        }

        setupRecyclerView()
        setupObservers()
        setupRefreshLayout()
        loadInitialData()
    }

    private fun setupRecyclerView() {
        appointmentsAdapter = DoctorAppointmentsAdapter()
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(this@DoctorAppointmentsActivity)
            adapter = appointmentsAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentViewModel.detailedAppointments.collect { appointments ->
                    Log.d(TAG, "Received ${appointments.size} detailed appointments")
                    appointmentsAdapter.submitList(appointments)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        // Observe errors
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentViewModel.uiState.collect { state ->
                    state.error?.let { error ->
                        showError(error)
                    }
                }
            }
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                loadDoctorAppointments()
            }
            setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        }
    }

    private fun loadInitialData() {
        binding.progressBar.visibility = View.VISIBLE
        loadDoctorAppointments()
    }

    private fun loadDoctorAppointments() {
        Log.d(TAG, "Loading appointments for doctor ID: $currentDoctorId")
        lifecycleScope.launch {
            try {
                appointmentViewModel.loadDoctorAppointments(currentDoctorId)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading appointments", e)
                showError("Error loading appointments: ${e.message}")
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
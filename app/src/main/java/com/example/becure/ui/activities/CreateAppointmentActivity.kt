package com.example.becure.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.becure.data.entities.Appointment
import com.example.becure.data.entities.Doctor
import com.example.becure.data.entities.Patient
import com.example.becure.databinding.ActivityCreateAppointmentBinding
import com.example.becure.repository.AppointmentRepository
import com.example.becure.repository.DoctorRepository
import com.example.becure.repository.PatientRepository
import com.example.becure.ui.viewmodels.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAppointmentBinding
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedDoctor: Doctor? = null
    private var currentPatient: Patient? = null
    private var doctors: List<Doctor> = emptyList()
    private var currentPatientId: Long = -1L

    private val doctorViewModel: DoctorViewModel by viewModels {
        DoctorViewModel.Factory(DoctorRepository(this))
    }

    private val patientViewModel: PatientViewModel by viewModels {
        PatientViewModel.Factory(PatientRepository(this))
    }

    private val appointmentViewModel: AppointmentViewModel by viewModels {
        AppointmentViewModel.Factory(
            AppointmentRepository(this),
            DoctorRepository(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get and validate patient ID first
        currentPatientId = intent.getLongExtra("PATIENT_ID", -1L)
        if (currentPatientId == -1L) {
            showError("No patient ID provided")
            finish()
            return
        }

        initializeViews()
        setupObservers()
        loadInitialData()
    }

    private fun initializeViews() {
        setupDoctorSelection()
        setupDateTimePickers()
        setupCreateButton()
    }

    private fun setupObservers() {
        // Observe patient data
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                patientViewModel.uiState.collectLatest { state ->
                    handlePatientState(state)
                }
            }
        }

        // Observe doctors data
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                doctorViewModel.uiState.collectLatest { state ->
                    handleDoctorsState(state)
                }
            }
        }

        // Observe appointment creation
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentViewModel.uiState.collectLatest { state ->
                    handleAppointmentState(state)
                }
            }
        }
    }

    private fun handlePatientState(state: PatientUiState) {
        when (val patientState = state.patients) {
            is UiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                val patient = patientState.data.find { it.patientId == currentPatientId }
                if (patient == null) {
                    showError("Invalid patient ID")
                    finish()
                } else {
                    currentPatient = patient
                }
            }
            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                showError("Error loading patient info: ${patientState.message}")
            }
        }
    }

    private fun handleDoctorsState(state: DoctorUiState) {
        when (state.doctors) {
            is UiState.Loading -> {
                binding.doctorSelectionLayout.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
            }
            is UiState.Success -> {
                binding.doctorSelectionLayout.isEnabled = true
                binding.progressBar.visibility = View.GONE
                doctors = state.doctors.data
                setupDoctorsDropdown(doctors)
            }
            is UiState.Error -> {
                binding.doctorSelectionLayout.isEnabled = true
                binding.progressBar.visibility = View.GONE
                showError(state.doctors.message)
            }
        }

        state.selectedDoctor?.let { doctor ->
            selectedDoctor = doctor
            binding.doctorSelection.setText("${doctor.name} - ${doctor.specialty}", false)
        }
    }

    private fun handleAppointmentState(state: AppointmentUiState) {
        if (state.error != null) {
            binding.progressBar.visibility = View.GONE
            binding.buttonCreateAppointment.isEnabled = true
            showError("Error: ${state.error}")
        }
    }

    private fun setupDoctorsDropdown(doctors: List<Doctor>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            doctors.map { "${it.name} - ${it.specialty}" }
        )

        binding.doctorSelection.apply {
            setAdapter(adapter)
            inputType = InputType.TYPE_NULL
            threshold = 1
            setOnClickListener { showDropDown() }
        }
    }

    private fun setupDoctorSelection() {
        binding.doctorSelection.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            setOnItemClickListener { _, _, position, _ ->
                doctors.getOrNull(position)?.let { doctor ->
                    selectedDoctor = doctor
                    doctorViewModel.selectDoctor(doctor.doctorId)
                }
            }
        }
    }

    private fun setupDateTimePickers() {
        binding.dateSelection.apply {
            isFocusable = false
            setOnClickListener { showDatePicker() }
        }

        binding.timeSelection.apply {
            isFocusable = false
            setOnClickListener { showTimePicker() }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, day)
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                selectedDate.set(Calendar.MINUTE, minute)
                updateTimeDisplay()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        binding.dateSelection.setText(dateFormat.format(selectedDate.time))
    }

    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.timeSelection.setText(timeFormat.format(selectedDate.time))
    }

    private fun setupCreateButton() {
        binding.buttonCreateAppointment.setOnClickListener {
            if (validateInputs()) {
                createAppointment()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (currentPatient == null) {
            showError("Invalid patient ID")
            return false
        }
        if (selectedDoctor == null) {
            showError("Please select a doctor")
            return false
        }
        if (binding.dateSelection.text.isNullOrEmpty()) {
            showError("Please select a date")
            return false
        }
        if (binding.timeSelection.text.isNullOrEmpty()) {
            showError("Please select a time")
            return false
        }

        // Validate selected time is not in the past
        if (selectedDate.timeInMillis < System.currentTimeMillis()) {
            showError("Please select a future date and time")
            return false
        }

        return true
    }

    private fun createAppointment() {
        val appointment = Appointment(
            doctorId = selectedDoctor!!.doctorId,
            patientId = currentPatientId,
            dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(selectedDate.time),
            status = "SCHEDULED",
            notes = ""
        )

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonCreateAppointment.isEnabled = false

                appointmentViewModel.createAppointment(appointment)

                Toast.makeText(
                    this@CreateAppointmentActivity,
                    "Appointment created successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.buttonCreateAppointment.isEnabled = true
                showError("Error creating appointment: ${e.message}")
            }
        }
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                // Load patient details
                patientViewModel.selectPatient(currentPatientId)

                // Load doctors list
                doctorViewModel.refreshDoctorsFromApi()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                showError("Error loading initial data: ${e.message}")
                finish()
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
package com.example.becure.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.becure.R
import com.example.becure.data.entities.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

abstract class BaseAppointmentsAdapter : ListAdapter<Appointment, RecyclerView.ViewHolder>(AppointmentDiffCallback()) {

    protected val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    protected val dateOnlyFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    protected val timeOnlyFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


        companion object {
            private const val VIEW_TYPE_PATIENT = 1
            private const val VIEW_TYPE_DOCTOR = 2
        }
        val appointmentList = mutableListOf<Appointment>()
        fun getAppointmentAtPosition(position: Int): Appointment {
            return appointmentList[position]
        }
        override fun getItemViewType(position: Int): Int {
            return if (appointmentList[position].isPatient) VIEW_TYPE_PATIENT else VIEW_TYPE_DOCTOR
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_PATIENT -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_patient_appointment, parent, false)
                    PatientViewHolder(view)
                }
                VIEW_TYPE_DOCTOR -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_appointment, parent, false)
                    DoctorViewHolder(view)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is PatientViewHolder -> holder.bind(appointmentList[position])
                is DoctorViewHolder -> holder.bind(appointmentList[position])
            }
        }

        override fun getItemCount(): Int = appointmentList.size

        inner class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(appointment: Appointment) {
                // Bind the data to the patient item layout
            }
        }

        inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(appointment: Appointment) {
                // Bind the data to the doctor item layout
            }
        }
    private class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem.appointmentId == newItem.appointmentId

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem == newItem
    }
}
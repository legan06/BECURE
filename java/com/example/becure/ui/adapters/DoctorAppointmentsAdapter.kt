package com.example.becure.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.becure.data.entities.AppointmentWithDetails
import com.example.becure.databinding.ItemPatientAppointmentBinding
import java.text.SimpleDateFormat
import java.util.*

class DoctorAppointmentsAdapter : ListAdapter<AppointmentWithDetails, DoctorAppointmentsAdapter.ViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPatientAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemPatientAppointmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        private val dateOnlyFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        private val timeOnlyFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(appointmentWithDetails: AppointmentWithDetails) {
            val date = dateFormat.parse(appointmentWithDetails.appointment.dateTime)

            binding.apply {
                // Show doctor name and details
                textDoctorName.text = appointmentWithDetails.doctor?.let { doctor ->
                    "Dr. ${doctor.name} - ${doctor.specialty}"
                } ?: "Loading doctor details..."

                // Show appointment date and time
                textAppointmentDate.text = date?.let { dateOnlyFormat.format(it) } ?: "Unknown Date"
                textAppointmentTime.text = date?.let { timeOnlyFormat.format(it) } ?: "Unknown Time"

                // Set status color based on appointment status
                when (appointmentWithDetails.appointment.status) {
                    "SCHEDULED" -> {
                        root.setBackgroundResource(android.R.color.white)
                    }
                    "COMPLETED" -> {
                        root.setBackgroundResource(android.R.color.holo_green_light)
                    }
                    "CANCELLED" -> {
                        root.setBackgroundResource(android.R.color.holo_red_light)
                    }
                }
            }
        }
    }

    private class AppointmentDiffCallback : DiffUtil.ItemCallback<AppointmentWithDetails>() {
        override fun areItemsTheSame(oldItem: AppointmentWithDetails, newItem: AppointmentWithDetails): Boolean =
            oldItem.appointment.appointmentId == newItem.appointment.appointmentId

        override fun areContentsTheSame(oldItem: AppointmentWithDetails, newItem: AppointmentWithDetails): Boolean =
            oldItem == newItem
    }
}
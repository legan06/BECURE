package com.example.becure.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.becure.data.entities.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

abstract class BaseAppointmentsAdapter : ListAdapter<Appointment, RecyclerView.ViewHolder>(AppointmentDiffCallback()) {

    protected val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    protected val dateOnlyFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    protected val timeOnlyFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem.appointmentId == newItem.appointmentId

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem == newItem
    }
}
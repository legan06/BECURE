package com.example.becure.network.models

data class DoctorResponse(
    val doctorId: Long,
    val name: String,
    val specialty: String,
    val phoneNumber: String,
    val email: String,
    val profileImage: String?
)

data class DoctorsWrapper(
    val doctors: List<DoctorResponse>?
)
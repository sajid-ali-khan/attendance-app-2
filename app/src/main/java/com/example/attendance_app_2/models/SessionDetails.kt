package com.example.attendance_app_2.models

//for creating a session row for every attendance taken
data class SessionDetails(val assignmentId: Int, val numPresent: Int, val numAbsent: Int, val date: String)

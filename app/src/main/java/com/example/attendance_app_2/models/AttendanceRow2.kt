package com.example.attendance_app_2.models

//overall attendance(all subjects)
data class AttendanceRow2(val roll: String, val name: String, val subjectAttendanceList: HashMap<String, Float>, val total: Float)

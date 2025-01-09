package com.example.attendance_app_2.models

//in the mark attendance fragment, to show the subjects assigned to the faculty
data class AssignedSubject(val assignmentId: String, val subjectCode: String, val className: String, var history: String)

package com.example.attendance_app_2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttendanceRow(val roll: String, val name: String, val percentages: List<Pair<Subject, Float>>): Parcelable

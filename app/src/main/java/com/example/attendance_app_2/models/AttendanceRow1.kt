package com.example.attendance_app_2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//for only one subject attendance
@Parcelize
data class AttendanceRow1(val roll: String, val name: String, val percentage: Float): Parcelable

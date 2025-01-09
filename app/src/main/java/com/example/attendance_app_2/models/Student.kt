package com.example.attendance_app_2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
//for submitting the attendance in the attendance table
@Parcelize
data class Student(val roll: String, val name: String, var attStatus: Boolean) : Parcelable

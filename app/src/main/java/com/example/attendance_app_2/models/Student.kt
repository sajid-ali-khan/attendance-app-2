package com.example.attendance_app_2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(val roll: String, val name: String, var attStatus: Boolean) : Parcelable

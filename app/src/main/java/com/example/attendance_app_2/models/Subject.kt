package com.example.attendance_app_2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subject(val id: Int, val code: String): Parcelable

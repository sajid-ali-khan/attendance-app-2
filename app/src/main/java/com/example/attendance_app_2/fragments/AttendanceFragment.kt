package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentAttendanceBinding

class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    lateinit var binding: FragmentAttendanceBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAttendanceBinding.bind(view)


    }
}
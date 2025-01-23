package com.example.attendance_app_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentSemesterDatesBinding

class SemesterDatesFragment : Fragment(R.layout.fragment_semester_dates) {
    private lateinit var bindings: FragmentSemesterDatesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings = FragmentSemesterDatesBinding.bind(view)
    }
}
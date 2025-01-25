package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentUpdateAttendanceBinding

class UpdateAttendanceFragment : Fragment(R.layout.fragment_update_attendance) {
    private lateinit var bindings : FragmentUpdateAttendanceBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings = FragmentUpdateAttendanceBinding.bind(view)

        bindings.btnFetchSessions.setOnClickListener{
            //get the entered date
            //validate the date
            //fetch assignments of that date
            //handle the clicks
            //fetch the students with the click
            //display students in a recycler view
        }
    }
}
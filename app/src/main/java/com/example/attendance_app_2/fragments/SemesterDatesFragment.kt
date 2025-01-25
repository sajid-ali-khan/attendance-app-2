package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentSemesterDatesBinding
import com.example.attendance_app_2.db.DatesHelper
import com.example.attendance_app_2.db.UpdateAttendanceHelper.validDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class SemesterDatesFragment : Fragment(R.layout.fragment_semester_dates) {
    private lateinit var bindings: FragmentSemesterDatesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings = FragmentSemesterDatesBinding.bind(view)

        lifecycleScope.launch {
            val dates = DatesHelper.fetchDates()
            bindings.etStartDate.setText(dates.startDate)
            bindings.etEndDate.setText(dates.endDate)
        }

        bindings.btnSave.setOnClickListener {
            Toast.makeText(requireContext(), "Saving dates", Toast.LENGTH_SHORT).show()
            val startDate = bindings.etStartDate.text.toString()
            val endDate = bindings.etEndDate.text.toString()
            if (validDate(startDate) && validDate(endDate)){
                lifecycleScope.launch {
                    val datesSaved = DatesHelper.saveDates(startDate, endDate)
                    var verdict = "Couldn't save dates"
                    if (datesSaved){
                        verdict = "Dates successfully saved"
                    }
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), verdict, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Invalid dates", Toast.LENGTH_SHORT).show()
            }

        }
    }


}
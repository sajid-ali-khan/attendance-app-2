package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.GenericStudentAdapter
import com.example.attendance_app_2.databinding.FragmentAttendanceBinding
import com.example.attendance_app_2.db.UpdateAttendanceHelper
import com.example.attendance_app_2.models.GenericStudent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AttendanceUpdatingFragment: Fragment(R.layout.fragment_attendance) {
    private lateinit var binding: FragmentAttendanceBinding
    var students = emptyList<GenericStudent>()
    var sessionId = 0
    lateinit var adapter: GenericStudentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAttendanceBinding.bind(view)

        arguments?.let {
            students = it.getParcelableArrayList<GenericStudent>("studentList") ?: emptyList()
            sessionId = it.getInt("sessionId")
        }

        displayStudents()

        binding.btnSaveAttednance.setOnClickListener{
            showSaveConfirmationDialog()
        }

        binding.btnAllAbsent.setOnClickListener{
            adapter.markAllAbsent()
        }

        binding.btnAllPresent.setOnClickListener{
            adapter.markAllPresent()
        }

        binding.btnCancel.setOnClickListener{
            showCancelConfirmationDialog()
        }

    }

    private fun displayStudents(){
        adapter = GenericStudentAdapter(students as MutableList<GenericStudent>)
        binding.rvStudentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudentList.adapter = adapter
    }

    private fun showSaveConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Submit the attendance?")
            .setPositiveButton("Yes") { _, _ ->
                updateAttendance { isSaved ->
                    if (isSaved) {
                        successDialog()
                    } else {
                        tryAgainDialog()
                    }
                }
            }
            .setNeutralButton("Not yet") { _, _ ->

            }
            .create()
            .show()
    }

    private fun showCancelConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Do you want to save your changes?")
            .setPositiveButton("Yes") { _, _ ->
                updateAttendance { isSaved ->
                    if (isSaved) {
                        successDialog()
                    } else {
                        tryAgainDialog()
                    }
                }
            }
            .setNegativeButton("No") { _, _ ->
                navigateBack()
            }
            .create()
            .show()
    }

    private fun updateAttendance(onComplete: (Boolean) -> Unit) {
        //collect the updated students list
        val updatedStudents = adapter.getUpdatedStudentList()
        val numPresent = updatedStudents.count { it.attStatus }
        val numAbsent = updatedStudents.size - numPresent
        //pass the students list (updated one) to the helper method
        lifecycleScope.launch {
            //update in the session table
            val sessionAck = UpdateAttendanceHelper.updateTheSession(requireContext(), sessionId, numPresent, numAbsent)
            if (!sessionAck){
                onComplete(false)
            }else{
                val ack = UpdateAttendanceHelper.updateAttendance(updatedStudents)
                onComplete(ack)
            }
        }
    }

    private fun successDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Success")
            .setMessage("Attendance saved successfully!")
            .setPositiveButton("Ok") { _, _ ->
                navigateBack()
            }
            .create()
            .show()
    }

    private fun tryAgainDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Try Again")
            .setMessage("Something went wrong!!Please try again.")
            .setPositiveButton("Ok") { _, _ ->

            }
            .create()
            .show()
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }
}
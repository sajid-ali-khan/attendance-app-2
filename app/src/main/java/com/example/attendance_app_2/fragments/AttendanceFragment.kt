package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.GenericStudentAdapter
import com.example.attendance_app_2.databinding.FragmentAttendanceBinding
import com.example.attendance_app_2.db.MarkAttendanceHelper
import com.example.attendance_app_2.models.GenericStudent
import com.example.attendance_app_2.models.SessionDetails
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    lateinit var binding: FragmentAttendanceBinding
    lateinit var assignmentId: String
    lateinit var studentsList: List<GenericStudent>
    lateinit var adapter: GenericStudentAdapter

    val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAttendanceBinding.bind(view)

        arguments?.let {
            assignmentId = it.getString("assignmentId", "")
            studentsList = it.getParcelableArrayList<GenericStudent>("studentList") ?: emptyList()
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
        adapter = GenericStudentAdapter(studentsList as MutableList<GenericStudent>)
        binding.rvStudentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudentList.adapter = adapter
    }

    private fun saveAttendance(onComplete: (Boolean) -> Unit) {
        lifecycleScope.launch {
            val updatedStudentList = adapter.getUpdatedStudentList()
            val numPresent = updatedStudentList.count { it.attStatus }
            val numAbsent = updatedStudentList.size - numPresent
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val sessionDetails = SessionDetails(assignmentId.toInt(), numPresent, numAbsent, date)

            val ack = MarkAttendanceHelper.saveAttendanceWithTimestamp(sessionDetails, updatedStudentList)
            onComplete(ack)
        }
    }


    private fun showSaveConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Submit the attendance?")
            .setPositiveButton("Yes") { _, _ ->
                saveAttendance { isSaved ->
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


    private fun showCancelConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Do you want to save your changes?")
            .setPositiveButton("Yes") { _, _ ->
                saveAttendance { isSaved ->
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

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }
}
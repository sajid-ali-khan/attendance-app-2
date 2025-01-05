package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.StudentAdapter
import com.example.attendance_app_2.databinding.FragmentAttendanceBinding
import com.example.attendance_app_2.db.MarkAttendanceHelper
import com.example.attendance_app_2.models.SessionDetails
import com.example.attendance_app_2.models.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    lateinit var binding: FragmentAttendanceBinding
    lateinit var assignmentId: String
    lateinit var studentsList: List<Student>
    lateinit var adapter: StudentAdapter

    val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAttendanceBinding.bind(view)

        arguments?.let {
            assignmentId = it.getString("assignmentId", "")
            studentsList = it.getParcelableArrayList<Student>("studentList") ?: emptyList()
        }

        displayStudents()

        binding.btnSaveAttednance.setOnClickListener{
            val updatedStudentList = adapter.getUpdatedStudentList()
            Log.d(TAG, "Updated Student List: $updatedStudentList")

            // Calculate numPresent and numAbsent
            val numPresent = updatedStudentList.count { it.attStatus }
            val numAbsent = updatedStudentList.size - numPresent

            // Get the current date in "yyyy-MM-dd" format
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


            val sessionDetails = SessionDetails(assignmentId.toInt(), numPresent, numAbsent, date)

            lifecycleScope.launch{
                val ack = MarkAttendanceHelper.saveAttendanceWithTimestamp(sessionDetails, updatedStudentList)

                withContext(Dispatchers.Main){
                    if(ack){
                        Toast.makeText(requireContext(), "Attendance saved successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Failed to save attendance", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnAllAbsent.setOnClickListener{
            adapter.markAllAbsent()
        }
        binding.btnAllPresent.setOnClickListener{
            adapter.markAllPresent()
        }
    }

    private fun displayStudents(){
        adapter = StudentAdapter(studentsList as MutableList<Student>)
        binding.rvStudentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudentList.adapter = adapter
    }
}
package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.databinding.FragmentDefAttReportBinding
import com.example.attendance_app_2.db.AttendanceReportHelper.generateSubjectAttendanceReport
import com.example.attendance_app_2.db.MarkAttendanceHelper.fetchFacultyAssignments
import com.example.attendance_app_2.models.AssignedSubject
import com.example.attendance_app_2.models.AttendanceRow1
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import kotlinx.coroutines.launch
import com.example.attendance_app_2.R

class DefaultAttReportFragment : Fragment(R.layout.fragment_def_att_report) {

    private lateinit var binding: FragmentDefAttReportBinding
    private val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDefAttReportBinding.inflate(inflater, container, false)
        val view = binding.root

        // Fetch employee ID from SharedPrefs
        val empId = SharedPrefs.getEmpId(requireContext())
        if (empId == null) {
            Toast.makeText(requireContext(), "Employee Id not found", Toast.LENGTH_SHORT).show()
            return view
        }

        // Fetch assigned subjects for the given empId
        lifecycleScope.launch {
            val assignedSubjects = getAssignedSubjects(empId)

            // Check if there are any assigned subjects
            if (assignedSubjects.isEmpty()) {
                Toast.makeText(requireContext(), "No class assigned to you.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Fetch and display the attendance report for the first assigned subject
            val assignmentId = assignedSubjects[0].assignmentId
            Log.d(TAG, "Fetching attendance report for assignmentId: $assignmentId")

            try {
                val attendanceReport = getAttendanceReport(assignmentId)
                Log.d(TAG, "Fetched attendance report size: ${attendanceReport.size}")

                // Here you can update the UI with the attendance report (e.g., populate a RecyclerView)
                // Example: binding.recyclerView.adapter = AttendanceAdapter(attendanceReport)

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching attendance report", e)
                Toast.makeText(requireContext(), "Failed to fetch attendance report", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Function to fetch assigned subjects for the employee
    private suspend fun getAssignedSubjects(empId: String): List<AssignedSubject> {
        return fetchFacultyAssignments(requireContext(), empId)
    }

    // Function to fetch the attendance report for a specific assignment
    private suspend fun getAttendanceReport(assignmentId: String): List<AttendanceRow1> {
        return generateSubjectAttendanceReport(requireContext(), assignmentId)
    }
}

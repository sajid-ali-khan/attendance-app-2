package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendance_app_2.databinding.FragmentDefAttReportBinding
import com.example.attendance_app_2.db.AttendanceReportHelper.generateSubjectAttendanceReport
import com.example.attendance_app_2.db.MarkAttendanceHelper.fetchFacultyAssignments
import com.example.attendance_app_2.models.AssignedSubject
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import kotlinx.coroutines.launch
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.AttendanceListAdapter
import com.example.attendance_app_2.fragments.ReportViewFragment
import com.example.attendance_app_2.models.AttendanceRow
import com.example.attendance_app_2.models.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

            withContext(Dispatchers.Main){
                val adapter = AttendanceListAdapter(assignedSubjects) { subject ->
                    onSubjectClick(subject)
                }
                binding.rvAssignedSubjects.layoutManager = LinearLayoutManager(requireContext())
                binding.rvAssignedSubjects.adapter = adapter
            }

        }

        return view
    }

    // Function to fetch assigned subjects for the employee
    private suspend fun getAssignedSubjects(empId: String): List<AssignedSubject> {
        return fetchFacultyAssignments(requireContext(), empId)
    }

    // Function to fetch the attendance report for a specific assignment
    private suspend fun getAttendanceReport(subject: Subject): List<AttendanceRow> {
        return generateSubjectAttendanceReport(requireContext(), subject)
    }

    private fun onSubjectClick(subject: Subject): Unit{
        lifecycleScope.launch {
            val attendanceReport = getAttendanceReport(subject)
            Log.d(TAG, "fetched Attendance Reprot: ${attendanceReport}")
            withContext(Dispatchers.Main){
                val reportViewFragment = ReportViewFragment()
                reportViewFragment.arguments = Bundle().apply {
                    putString("flag", "1")
                    putParcelableArrayList("attendanceReport", ArrayList(attendanceReport))
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, reportViewFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

}

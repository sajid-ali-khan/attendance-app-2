package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.AssignedSubjectAdapter
import com.example.attendance_app_2.databinding.FragmentMarkAttendanceBinding
import com.example.attendance_app_2.db.MarkAttendanceHelper
import com.example.attendance_app_2.models.AssignedSubject
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkAttendanceFragment : Fragment(R.layout.fragment_mark_attendance) {
    lateinit var binding: FragmentMarkAttendanceBinding

    lateinit var rvAssignmentsList : RecyclerView

    val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMarkAttendanceBinding.bind(view)

        rvAssignmentsList = binding.rvAssignmentsList

        val empId = SharedPrefs.getEmpId(requireContext())

        //fetch the subjects assigned to the current faculty
        lifecycleScope.launch {
            if (empId == null){
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "Employee ID not found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val assignedSubjects = MarkAttendanceHelper.fetchFacultyAssignments(requireContext(), empId)
            withContext(Dispatchers.Main){
                showAssignedSubjects(assignedSubjects)
            }
        }

    }

    fun onClassClick(assignmentId: String){
        lifecycleScope.launch{
            val students = MarkAttendanceHelper.fetchStudents(requireContext(), assignmentId)
            Log.d(TAG, "fetched Students = ${students}")
            withContext(Dispatchers.Main){
                safeNavigate {
                    val attendanceFragment = AttendanceFragment()
                    attendanceFragment.arguments = Bundle().apply {
                        putParcelableArrayList("studentList", ArrayList(students))
                        putString("assignmentId", assignmentId)
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, attendanceFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun showAssignedSubjects(assignedSubjects: List<AssignedSubject>){
        val adapter = AssignedSubjectAdapter(assignedSubjects){ assignmentId ->
            onClassClick(assignmentId)
        }
        rvAssignmentsList.layoutManager = LinearLayoutManager(requireContext())
        rvAssignmentsList.adapter = adapter
    }

    fun safeNavigate(action: () -> Unit) {
        if (isAdded && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            action()
        }
    }

}
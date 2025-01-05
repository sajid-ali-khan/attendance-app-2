package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.db.SeeAssignmentsHelper.formClassName
import com.example.attendance_app_2.models.AssignedSubject
import com.example.attendance_app_2.models.Student
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.attendance_app_2.R

object MarkAttendanceHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchFacultyAssignments(context: Context, empId: String): List<AssignedSubject> {
        val query = context.getString(R.string.query_fetchSubjectAssigned)

        return withContext(Dispatchers.IO) {
            val facultyAssignments = mutableListOf<AssignedSubject>()
            try {
                DatabaseHelper.getConnection()?.use {connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, empId)
                        pst.executeQuery().use {
                            while (it.next()){
                                val assignmentId = it.getString("assignment_id")
                                val scode = it.getString("scode")
                                val branch = it.getString("branch")
                                val sem = it.getString("sem")
                                val section = it.getString("section")
                                val className = formClassName(branch, sem, section)
                                facultyAssignments.add(AssignedSubject(assignmentId, scode, className, ""))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchFacultyAssignments: SQL error", e)
            }
            facultyAssignments.toList()
        }
    }

    suspend fun fetchStudents(context: Context, assignment_id: String): List<Student>{
        val query = context.getString(R.string.query_fetchStudents)

        return withContext(Dispatchers.IO){
            var students = mutableListOf<Student>()
            try{
                DatabaseHelper.getConnection()?.use{connection->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, assignment_id)
                        pst.executeQuery().use {
                            while(it.next()){
                                val roll = it.getString("ROLLNO")
                                val name = it.getString("NAME")
                                students.add(Student(roll, name, false))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchStudents: SQL error", e)
            }
            students
        }
    }
}
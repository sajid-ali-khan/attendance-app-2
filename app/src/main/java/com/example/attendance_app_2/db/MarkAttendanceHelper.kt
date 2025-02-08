package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.db.SeeAssignmentsHelper.formClassName
import com.example.attendance_app_2.models.AssignedSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.attendance_app_2.R
import com.example.attendance_app_2.models.GenericStudent
import com.example.attendance_app_2.models.SessionDetails
import com.example.attendance_app_2.models.Subject
import java.sql.Statement

object MarkAttendanceHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchFacultyAssignments(context: Context, empId: String): List<AssignedSubject> {
        val query = context.getString(R.string.query_fetchSubjectAssigned)

        return withContext(Dispatchers.IO) {
            val facultyAssignments = mutableListOf<AssignedSubject>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, empId)
                        pst.executeQuery().use {
                            while (it.next()){
                                val assignmentId = it.getInt("assignment_id")
                                val scode = it.getString("scode")
                                val branch = it.getString("branch")
                                val sem = it.getString("sem")
                                val section = it.getString("section")
                                val className = formClassName(branch, sem, section)
                                facultyAssignments.add(AssignedSubject(Subject(assignmentId, scode), className))
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

    suspend fun fetchStudents(context: Context, assignment_id: String): List<GenericStudent>{
        val query = context.getString(R.string.query_fetchStudents)

        return withContext(Dispatchers.IO){
            var students = mutableListOf<GenericStudent>()
            try{
                DatabaseHelper.getConnection()?.use{ connection->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, assignment_id)
                        pst.executeQuery().use {
                            while(it.next()){
                                val roll = it.getString("ROLL")
                                val name = it.getString("NAME")
                                students.add(GenericStudent(roll, name, false))
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

    suspend fun saveAttendanceWithTimestamp(sessionDetails: SessionDetails, students: List<GenericStudent>): Boolean {
        val sessionQuery = "INSERT INTO session (assignment_id, num_present, num_absent, date) VALUES (?, ?, ?, ?)"
        val attendanceQuery = "INSERT INTO attendance (session_id, roll, status) VALUES (?, ?, ?)"

        return withContext(Dispatchers.IO) {
            var sessionId = -1
            var result = false
            try{
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.autoCommit = false

                    try{
                        connection.prepareStatement(sessionQuery, Statement.RETURN_GENERATED_KEYS).use {pst ->
                            pst.setInt(1, sessionDetails.assignmentId)
                            pst.setInt(2, sessionDetails.numPresent)
                            pst.setInt(3, sessionDetails.numAbsent)
                            pst.setDate(4, java.sql.Date.valueOf(sessionDetails.date))

                            pst.executeUpdate()

                            val generatedKeys = pst.generatedKeys
                            if (generatedKeys.next()){
                                sessionId = generatedKeys.getInt(1)
                            }
                        }

                        connection.prepareStatement(attendanceQuery).use {
                            for (student in students) {
                                it.setInt(1, sessionId)
                                it.setString(2, student.roll)
                                it.setBoolean(3, student.attStatus)
                                it.addBatch()
                            }

                            it.executeBatch()
                        }
                        connection.commit()
                        result = true
                    }catch(e: Exception){
                        connection.rollback()
                        Log.e(TAG, "saveAttendanceWithTimestamp: SQL error, rolling back", e)
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "saveAttendanceWithTimestamp: SQL error", e)
            }
            Log.d(TAG, "The result of submitting: $result")
            result
        }
    }


}
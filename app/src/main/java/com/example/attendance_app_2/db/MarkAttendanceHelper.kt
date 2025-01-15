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
                DatabaseHelper.getConnection()?.use {connection ->
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

    suspend fun fetchTodaySessionDetails(context: Context, assignmentId: String):String{
        val query = context.getString(R.string.query_fetchTodaySessionDetails)
        return withContext(Dispatchers.IO){
            val history: StringBuilder = StringBuilder()
            try{
                DatabaseHelper.getConnection()?.use {connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, assignmentId)
                        pst.executeQuery().use {
                            while(it.next()) {
                                val timestamp = it.getString("timestamp")
                                val numPresent = it.getInt("num_present")
                                val total = it.getInt("num_absent") + numPresent
                                history.append("$timestamp: $numPresent/$total\n")
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchTodaySessionDetails: SQL error", e)
            }
            history.toString()
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

    suspend fun saveAttendanceWithTimestamp(sessionDetails: SessionDetails, students: List<Student>): Boolean {
        val sessionQuery = "INSERT INTO session (assignment_id, num_present, num_absent, date) VALUES (?, ?, ?, ?)"
        val attendanceQuery = "INSERT INTO attendance (session_id, student_id, status) VALUES (?, ?, ?)"

        return withContext(Dispatchers.IO) {
            var sessionId = -1
            var result = false
            try{
                DatabaseHelper.getConnection()?.use {connection ->
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
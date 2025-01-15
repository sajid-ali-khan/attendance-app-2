package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.models.AttendanceRow
import com.example.attendance_app_2.models.Subject
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AttendanceReportHelper {
    val TAG = this::class.java.simpleName

    suspend fun generateSubjectAttendanceReport(context: Context, subject: Subject): List<AttendanceRow> {
        // SQL procedure call string
        val procedureCall = "EXEC GetAttendanceByAssignments @AssignmentIDs = ?"
        val attendanceReport = mutableListOf<AttendanceRow>()

        // Ensure that assignmentId is not empty
        if (subject.id.toString().isBlank()) {
            Log.e(TAG, "generateSubjectAttendanceReport: Invalid assignmentId - it is blank")
            return attendanceReport
        }

        return withContext(Dispatchers.IO) {
            try {
                DatabaseHelper.getConnection()?.use{connection ->
                    connection.prepareStatement(procedureCall).use { pst ->
                        pst.setString(1, subject.id.toString())

                        pst.executeQuery().use {resultSet ->
                            while (resultSet.next()) {
                                val roll = resultSet.getString("roll")
                                val name = resultSet.getString("name")
                                val percentage = resultSet.getFloat("Assignment_"+subject.id)

                                attendanceReport.add(AttendanceRow(roll, name, hashMapOf(Pair(subject, percentage))))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "generateSubjectAttendanceReport: SQL error", e)
            }
            attendanceReport
        }
    }
}

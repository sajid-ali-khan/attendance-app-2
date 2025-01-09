package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.models.AttendanceRow1
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AttendanceReportHelper {
    val TAG = this::class.java.simpleName

    suspend fun generateSubjectAttendanceReport(context: Context, assignmentId: String): List<AttendanceRow1> {
        // SQL procedure call string
        val procedureCall = "EXEC GetAttendanceByAssignments @AssignmentIDs = ?"
        val attendanceReport = mutableListOf<AttendanceRow1>()

        // Ensure that assignmentId is not empty
        if (assignmentId.isBlank()) {
            Log.e(TAG, "generateSubjectAttendanceReport: Invalid assignmentId - it is blank")
            return attendanceReport
        }

        return withContext(Dispatchers.IO) {
            try {
                DatabaseHelper.getConnection()?.use{connection ->
                    connection.prepareStatement(procedureCall).use { pst ->
                        pst.setString(1, assignmentId)

                        pst.executeQuery().use {resultSet ->
                            while (resultSet.next()) {
                                val roll = resultSet.getString("roll")
                                val name = resultSet.getString("name")
                                val percentage = resultSet.getFloat("Assignment_"+assignmentId)

                                attendanceReport.add(AttendanceRow1(roll, name, percentage))
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

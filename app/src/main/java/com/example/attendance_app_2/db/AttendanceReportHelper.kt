package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.models.AttendanceRow
import com.example.attendance_app_2.models.SemesterDates
import com.example.attendance_app_2.models.Subject
import com.example.attendance_app_2.models.SubjectAttendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AttendanceReportHelper {
    val TAG = this::class.java.simpleName

    suspend fun generateSubjectAttendanceReport(context: Context, subject: Subject): List<AttendanceRow> {
        // SQL procedure call string
        val procedureCall = "EXEC GetAttendanceTotal @AssignmentIDs = ?"
        val attendanceReport = mutableListOf<AttendanceRow>()

        // Ensure that assignmentId is not empty
        if (subject.id.toString().isBlank()) {
            Log.e(TAG, "generateSubjectAttendanceReport: Invalid assignmentId - it is blank")
            return attendanceReport
        }

        return withContext(Dispatchers.IO) {
            try {
                DatabaseHelper.getConnection()?.use{ connection ->
                    connection.prepareStatement(procedureCall).use { pst ->
                        pst.setString(1, subject.id.toString())

                        pst.executeQuery().use {resultSet ->
                            while (resultSet.next()) {
                                val roll = resultSet.getString("rollnumber")
                                val name = resultSet.getString("name")
                                val percentage = resultSet.getFloat("AttendancePercentage")

                                attendanceReport.add(AttendanceRow(roll, name, listOf(
                                    SubjectAttendance(subject, percentage)
                                )))
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

    suspend fun generateAllAttendanceReport(context: Context, assignmentIds: List<Subject>, dates: SemesterDates): List<AttendanceRow> {
        val procedureCall = "EXEC GetAttendance @AssignmentIDs = ?, @StartDate = ?, @EndDate = ?"
        val assignments = assignmentIds.joinToString(",") { it.id.toString() }

        return withContext(Dispatchers.IO) {
            val attendanceReport = mutableListOf<AttendanceRow>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(procedureCall).use { pst ->
                        pst.setString(1, assignments)
                        pst.setString(2, dates.startDate)
                        pst.setString(3, dates.endDate)

                        pst.executeQuery().use { resultSet ->
                            while (resultSet.next()) {
                                val roll = resultSet.getString("roll")
                                val name = resultSet.getString("name")
                                val percentages = mutableListOf<SubjectAttendance>()
                                for (subject in assignmentIds) {
                                    val percentage = resultSet.getFloat("Assignment_"+subject.id)
                                    percentages.add(SubjectAttendance(subject, percentage))
                                }
                                percentages.add(SubjectAttendance(Subject(-1, "Total"), resultSet.getFloat("TotalAttendance")))
                                attendanceReport.add(AttendanceRow(roll, name, percentages))
                            }
                        }
                    }
                }
            }catch( e: Exception) {
                Log.e(TAG, "generateAllAttendanceReport: SQL error", e)
            }
            attendanceReport
        }
    }
}

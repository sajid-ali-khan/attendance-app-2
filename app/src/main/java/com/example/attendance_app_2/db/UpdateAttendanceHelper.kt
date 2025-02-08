package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.R
import com.example.attendance_app_2.models.GenericStudent
import com.example.attendance_app_2.models.UpdateCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object UpdateAttendanceHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchSessions(context: Context, date: String, facultyId: String): List<UpdateCard>{
        val query = context.getString(R.string.queryFetchSessions)
        return withContext(Dispatchers.IO){
            val sessions = mutableListOf<UpdateCard>()
            try{
                DatabaseHelper.getConnection()?.use{ connection ->
                    connection.prepareStatement(query).use{pst ->
                        pst.setString(1, date)
                        pst.setString(2, facultyId)
                        pst.executeQuery()?.use{
                            while(it.next()){
                                val sessionId = it.getInt("session_id")
                                val scode = it.getString("scode")
                                val branch = it.getString("branch")
                                val sem = it.getString("sem")
                                val section = it.getString("section")
                                val numPresent = it.getInt("num_present")
                                val numAbsent = it.getInt("num_absent")
                                val timestamp = it.getString("timestamp")
                                val className = SeeAssignmentsHelper.formClassName(branch, sem, section)
                                sessions.add(UpdateCard(sessionId, scode, className, timestamp, "$numPresent/${numAbsent + numPresent}"))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchSessions: SQL error", e)
            }
            sessions
        }
    }

    suspend fun fetchStudents(context: Context, sessionId: Int): List<GenericStudent>{
        val query = context.getString(R.string.queryFetchStudents)
        return withContext(Dispatchers.IO){
            val students = mutableListOf<GenericStudent>()
            try{
                DatabaseHelper.getConnection()?.use{ connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setInt(1, sessionId)
                        pst.executeQuery().use {
                            while (it.next()){
                                val attendanceId = it.getInt("attendance_id")
                                val roll = it.getString("roll")
                                val name = it.getString("name")
                                val status = it.getBoolean("status")
                                students.add(GenericStudent(roll, name, status, attendanceId))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchStudents: SQL Error", e)
            }
            students
        }

    }

    fun validDate(dateString: String): Boolean {
        val originalFormat = DateTimeFormatter.ofPattern("dd/MM/yy")
        return try {
            val sqlServerFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(dateString, originalFormat)

            date.format(sqlServerFormat)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    suspend fun updateAttendance(updatedStudents: List<GenericStudent>): Boolean {
        val updateQuery = "UPDATE attendance SET status = ? WHERE attendance_id = ?"
        return withContext(Dispatchers.IO){
            var ack = false
            try{
                DatabaseHelper.getConnection()?.use{ connection ->
                    connection.autoCommit = false
                    try{
                        connection.prepareStatement(updateQuery).use {
                            for (student in updatedStudents) {
                                it.setBoolean(1, student.attStatus)
                                it.setInt(2, student.attendanceId!!)
                                it.addBatch()
                            }
                            it.executeBatch()
                        }
                        connection.commit()
                        ack = true
                    }catch(e: Exception){
                        connection.rollback()
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "updateAttendance: Couldn't update the attendance", e)
            }
            ack
        }
    }

    suspend fun updateTheSession(context: Context, sessionId: Int, numPresent: Int, numAbsent: Int): Boolean {
        val query = context.getString(R.string.queryUpdateSession)

        return withContext(Dispatchers.IO){
            var ack = false
            try{
                DatabaseHelper.getConnection()?.use{ connection ->
                    connection.prepareStatement(query).use {
                        it.setInt(1, numPresent)
                        it.setInt(2, numAbsent)
                        it.setInt(3, sessionId)
                        val rowsUpdated = it.executeUpdate()
                        if (rowsUpdated == 1) ack = true
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "updateTheSession: SQL Error", e)
            }
            ack
        }
    }
}
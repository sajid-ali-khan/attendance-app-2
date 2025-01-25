package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.R
import com.example.attendance_app_2.models.UpdateCard
import com.example.demokotlin.DatabaseHelper
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
                DatabaseHelper.getConnection()?.use{connection ->
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

    fun validDate(dateString: String): Boolean {
        val originalFormat = DateTimeFormatter.ofPattern("dd/MM/yy")
        return try {
            val sqlServerFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(dateString, originalFormat)

            val sqlDate = date.format(sqlServerFormat)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
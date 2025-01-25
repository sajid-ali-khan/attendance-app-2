package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.R
import com.example.attendance_app_2.models.UpdateCard
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UpdateAttendanceHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchSessions(context: Context, date: String): List<UpdateCard>{
        val query = context.getString(R.string.queryFetchSessions)
        return withContext(Dispatchers.IO){
            val sessions = mutableListOf<UpdateCard>()
            try{
                DatabaseHelper.getConnection()?.use{connection ->
                    connection.prepareStatement(query).use{pst ->
                        pst.setString(1, date)
                        pst.executeQuery()?.use{
                            while(it.next()){
                                val sessionId = it.getInt("session_id")
                                val scode = it.getString("scode")
                                val branch = it.getString("branch")
                                val sem = it.getString("sem")
                                val section = it.getString("section")
                                val className = SeeAssignmentsHelper.formClassName(branch, sem, section)
                                sessions.add(UpdateCard(sessionId, scode, className))
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
}
package com.example.attendance_app_2.db

import android.util.Log
import com.example.attendance_app_2.models.SemesterDates
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatesHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchDates(): SemesterDates {
        val query = "SELECT start_date, end_date FROM semester_dates"
        return withContext(Dispatchers.IO){
            var date = SemesterDates("", "")
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.createStatement().use { st ->
                        st.executeQuery(query).use {
                            val startDate= it.getString("start_date")
                            val endDate = it.getString("end_date")
                            date = SemesterDates(startDate, endDate)
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchDates: SQl error", e)
            }
            date
        }
    }
}
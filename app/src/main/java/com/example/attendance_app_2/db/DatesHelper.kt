package com.example.attendance_app_2.db

import android.util.Log
import com.example.attendance_app_2.models.SemesterDates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatesHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchDates(): SemesterDates {
        val query = "select convert(varchar, start_date, 3) as start_date, convert(varchar, end_date, 3) as end_date from semester_dates"
        return withContext(Dispatchers.IO){
            var date = SemesterDates("", "")
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.createStatement().use { st ->
                        st.executeQuery(query).use {
                            if (it.next()) {
                                val startDate= it.getString("start_date")
                                val endDate = it.getString("end_date")
                                date = SemesterDates(startDate, endDate)
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchDates: SQl error", e)
            }
            date
        }
    }

    suspend fun saveDates(startDate: String, endDate: String): Boolean{
        val query = "UPDATE semester_dates SET start_date = CONVERT(DATE, ?, 3),\n" +
                "    end_date = CONVERT(DATE, ?, 3)\n" +
                "WHERE id = 1"
        return withContext(Dispatchers.IO){
            var datesSaved = false
            try{
                DatabaseHelper.getConnection()?.use{ connection->
                    connection.prepareStatement(query).use{pst ->
                        pst.setString(1, startDate)
                        pst.setString(2, endDate)
                        val rowsAffected = pst.executeUpdate()
                        if (rowsAffected == 1){
                            datesSaved = true
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "saveDates: SQL error", e)
            }
            datesSaved
        }
    }
}
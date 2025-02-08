package com.example.attendance_app_2.auth

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import com.example.attendance_app_2.db.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException

object AuthHelper {

    private const val TAG = "AuthHelper"

    suspend fun authenticate(context: Context, empId: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val connection = DatabaseHelper.getConnection()

                if (connection == null) {
                    Log.e(TAG, "Failed to obtain a database connection")
                    return@withContext AuthResult.UNKNOWN_ERROR
                }

                val query = "SELECT empid, name, pwd, role FROM faculty WHERE empid = ?"

                connection.prepareStatement(query).use { pst ->
                    pst.setString(1, empId)
                    pst.executeQuery().use { rs ->
                        if (rs.next()) {
                            val dbPassword = rs.getString("pwd")
                            val dbRole = rs.getString("role")
                            val dbName = rs.getString("name")
                            if (dbPassword == password) {
                                // Save user details in SharedPreferences
                                SharedPrefs.saveUserDetails(context, empId, dbName, dbRole)
                                AuthResult.SUCCESS
                            } else {
                                AuthResult.INVALID_PASSWORD
                            }
                        } else {
                            AuthResult.USER_NOT_FOUND
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.e(TAG, "Database error during authentication", e)
                AuthResult.UNKNOWN_ERROR
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during authentication", e)
                AuthResult.UNKNOWN_ERROR
            }
        }
    }
}

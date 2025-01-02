package com.example.attendance_app_2.auth

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.db.HikariCPDataSource
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException


object AuthHelper {

    val TAG = "AuthHelper"
    var connection: Connection? = null
    suspend fun authenticate(context: Context, empId: String, password: String): AuthResult {
        try{
            connection = HikariCPDataSource.getConnection()
        }catch(e: Exception){
            return AuthResult.UNKNOWN_ERROR
        }

        val query = "SELECT empid, name, pwd, role FROM faculty WHERE empid = ?"

        return withContext(Dispatchers.IO){
            try {
                HikariCPDataSource.getConnection().use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setString(1, empId)
                        pst.executeQuery().use { rs ->
                            if (rs.next()) {
                                val dbPassword = rs.getString("pwd")
                                val dbRole = rs.getString("role")
                                val dbName = rs.getString("name")
                                if (dbPassword == password) {
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
                }
            } catch (e: SQLException) {
                Log.d(TAG, "authenticate: ERROR!!" + e.message)
                AuthResult.UNKNOWN_ERROR
            }
        }
    }
}
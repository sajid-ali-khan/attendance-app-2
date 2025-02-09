package com.example.attendance_app_2.db

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

object DatabaseHelper {
    var con: Connection? = null

    suspend fun getConnection(): Connection? = withContext(Dispatchers.IO){
        val ip = "192.168.41.44"
        val port = "1433"
        val dbname = "test"
        val un = "sajid"
        val pass = "S@j1d2024!"

        val conURL: String
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            conURL = "jdbc:jtds:sqlserver://$ip:$port;databasename=$dbname;user=$un;password=$pass;"
            con = DriverManager.getConnection(conURL)
        } catch (e: Exception) {
            Log.e("Error1", e.message ?: "Unknown error")
        }
        con
    }
}
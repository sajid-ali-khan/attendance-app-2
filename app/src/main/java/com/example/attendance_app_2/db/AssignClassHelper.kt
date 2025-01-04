package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException

object AssignClassHelper {
    const val TAG = "AssignClassHelper"
    suspend fun doesAssignmentAlreadyExists(context: Context, facultyId: String, courseId: String, section: String): List<Any> {
        val query = "SELECT assignment_id FROM assignments WHERE faculty_id = ? AND course_id = ? AND section = ?"
        return withContext(Dispatchers.IO) {
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setString(1, facultyId)
                        pst.setString(2, courseId)
                        pst.setString(3, section)
                        pst.executeQuery().use { rs ->
                            if (rs.next()) {
                                return@withContext listOf(true, rs.getString("assignment_id"))
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                Log.e(TAG, "doesAssignmentAlreadyExists: SQL error occurred.", e)
            } catch (e: Exception) {
                Log.e(TAG, "doesAssignmentAlreadyExists: Unexpected error occurred.", e)
            }
            listOf(false, "")
        }
    }

    suspend fun assignClass(context: Context, facultyId: String, courseId: String, section: String): Boolean {
        val query = "INSERT INTO assignments (faculty_id, course_id, section) VALUES (?, ?, ?)"
        return withContext(Dispatchers.IO){
            var result = false
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, facultyId)
                        pst.setInt(2, courseId.toInt())
                        pst.setString(3, section)
                        val rowsAffected = pst.executeUpdate()
                        result = rowsAffected == 1
                    }
                }
            }catch (e: SQLException) {
                Log.e(TAG, "assignClass: SQL error occurred. Faculty ID: $facultyId, Course ID: $courseId, Section: $section", e)
            } catch (e: Exception) {
                Log.e(TAG, "assignClass: Unexpected error occurred.", e)
            }
            result
        }
    }

}
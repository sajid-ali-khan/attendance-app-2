package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.models.Course
import com.example.attendance_app_2.models.Faculty
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataFetcher {
    const val TAG = "DataFetcher"
    suspend fun fetchSchemes(context: Context): MutableList<String> {
        val query = "SELECT DISTINCT scheme FROM course"
        return withContext(Dispatchers.IO) {
            val schemes = mutableListOf<String>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.executeQuery().use { rs ->
                            while (rs.next()) {
                                schemes.add(rs.getString("scheme"))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchSchemes: Error", e)
            }
            schemes
        }
    }

    suspend fun fetchBranches(context: Context, scheme: String): MutableList<Int> {
        val query = "SELECT DISTINCT LEFT(branch, 1) as branch_code FROM course WHERE scheme = ?"
        return withContext(Dispatchers.IO) {
            val branches = mutableListOf<Int>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setString(1, scheme) // Set the parameter value for the query
                        pst.executeQuery().use { rs ->
                            while (rs.next()) {
                                branches.add(rs.getInt("branch_code"))
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e(TAG, "fetchBranches: Error", e)
            }
            branches
        }
    }

    suspend fun fetchSemesters(context: Context, scheme: String, branch: String): MutableList<Int> {
        val query = "SELECT DISTINCT sem FROM course WHERE scheme = ? AND LEFT(branch, 1) = ?"
        return withContext(Dispatchers.IO) {
            val semesters = mutableListOf<Int>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setString(1, scheme)
                        pst.setString(2, branch)
                        pst.executeQuery().use { rs ->
                            while (rs.next()) {
                                semesters.add(rs.getInt("sem"))
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e(TAG, "fetchSemesters: Error", e)
            }
            Log.d(TAG, "fetchSemesters: ${semesters}")
            semesters
        }
    }

    suspend fun fetchSections(context: Context, branch: String, semester: String): MutableList<String> {
        val query = "SELECT DISTINCT sec FROM students WHERE LEFT(branch, 1) = ? AND sem = ?"
        return withContext(Dispatchers.IO) {
            val sections = mutableListOf<String>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use { pst ->
                        pst.setString(1, branch)
                        pst.setString(2, semester)
                        pst.executeQuery().use { rs ->
                            while (rs.next()) {
                                sections.add(rs.getString("sec"))
                            }
                        }
                    }
                }
            }catch(e: Exception) {
                Log.e(TAG, "fetchSections: Error", e)
            }
            Log.d(TAG, "fetchSections: fetched sections = ${sections}")
            sections
        }
    }

    suspend fun fetchSubjects(context: Context,scheme: String, branch: String, semester: String): MutableList<Course> {
        val query = "SELECT courseid, scode, subname FROM course WHERE scheme = ? AND LEFT(branch, 1) = ? AND sem = ?"
        return withContext(Dispatchers.IO) {
            val subjects = mutableListOf<Course>()
            try {
                DatabaseHelper.getConnection()?.use { connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, scheme)
                        pst.setString(2, branch)
                        pst.setString(3, semester)
                        pst.executeQuery().use {
                            while (it.next()) {
                                subjects.add(
                                    Course(
                                        it.getInt("courseid"),
                                        it.getString("subname"),
                                        it.getString("scode")
                                    )
                                )
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e(TAG, "fetchSubjects: Error", e)
            }
            subjects
        }
    }

    suspend fun fetchFaculties(): MutableList<Faculty> {
        val query = "SELECT empid, name FROM FACULTY"
        return withContext(Dispatchers.IO) {
            val faculties = mutableListOf<Faculty>()
            try{
                DatabaseHelper.getConnection()?.use {connection ->
                    connection.createStatement().use {st ->
                        st.executeQuery(query).use {
                            while (it.next()) {
                                val empid = it.getString("empid")?:"xxxx"
                                val name = it.getString("name")?:"xxxxxxxxxx"
                                faculties.add(Faculty(empid, name))
                            }
                        }
                    }
                }
            }catch(e:Exception){
                Log.e(TAG, "fetchFaculties: Error", e)
            }
            faculties
        }
    }
}
package com.example.attendance_app_2.db

import android.content.Context
import android.util.Log
import com.example.attendance_app_2.models.Assignment
import com.example.attendance_app_2.models.Subject
import com.example.attendance_app_2.utils.BranchYearMapper
import com.example.demokotlin.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeeAssignmentsHelper {
    val TAG = this::class.java.simpleName
    suspend fun fetchAssignments(context: Context, scheme: String, branch: String, semester:String, section: String): List<Assignment>{
        val query = "with FilteredCourses as (\n" +
                "    select courseid, scode, subname\n" +
                "    from course\n" +
                "    where scheme = ? and left(branch, 1) = ? and sem = ?\n" +
                "),\n" +
                "CourseAssignments as (\n" +
                "    select course.scode, course.SUBNAME, assignments.faculty_id\n" +
                "    from assignments\n" +
                "    inner join FilteredCourses course\n" +
                "    on course.courseid = assignments.course_id\n" +
                "    where assignments.section = ?\n" +
                ")\n" +
                "select result.scode, result.subname, faculty.name, faculty.EMPID\n" +
                "from CourseAssignments result\n" +
                "inner join faculty\n" +
                "on result.faculty_id = faculty.EMPID\n"

        return withContext(Dispatchers.IO) {
            val assignments = mutableListOf<Assignment>()
            try {
                DatabaseHelper.getConnection()?.use {connection ->
                    connection.prepareStatement(query).use {pst ->
                        pst.setString(1, scheme)
                        pst.setString(2, branch)
                        pst.setString(3, semester)
                        pst.setString(4, section)
                        pst.executeQuery().use {
                            while (it.next()){
                                val scode = it.getString("scode")
                                val subname = it.getString("subname")
                                val facultyName = it.getString("name")
                                val facultyId = it.getString("empid")
                                assignments.add(Assignment(scode, subname, facultyName, facultyId))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.d(TAG, "fetchAssignments: SQL error occured", e)
            }
            assignments.toList()
        }
    }

    fun formClassName(branch: String, semester: String, section: String): String {
        val branchName = BranchYearMapper.getBranchName(branch.toInt())
        val semester = semester.toInt()

        val ext = when(semester % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
        return "$semester$ext sem $branchName $section-section"

    }

    suspend fun fetchAssignmentIds(branch: String, semester: String, section: String): MutableList<Subject> {
        val query = "SELECT assignments.assignment_id, course.scode\n" +
                "FROM assignments\n" +
                "INNER JOIN course\n" +
                "on assignments.course_id = course.courseid\n" +
                "WHERE left(course.branch, 1) = ?\n" +
                "AND course.sem = ?\n" +
                "AND assignments.section = ?"
        return withContext(Dispatchers.IO){
            val assignmentIds = mutableListOf<Subject>()
            try {
                DatabaseHelper.getConnection()?.use{connection ->
                    connection.prepareStatement(query)?.use{pst ->
                        pst.setString(1, branch)
                        pst.setString(2, semester)
                        pst.setString(3, section)
                        pst.executeQuery().use {
                            while (it.next()) {
                                val id = it.getInt("assignment_id")
                                val scode = it.getString("scode")
                                assignmentIds.add(Subject(id, scode))
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, "fetchAssignmentIds: SQL error",e)
            }
            assignmentIds
        }
    }
}
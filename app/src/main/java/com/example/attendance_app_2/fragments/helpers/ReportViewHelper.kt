package com.example.attendance_app_2.fragments.helpers

import android.content.Context
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.attendance_app_2.R
import com.example.attendance_app_2.models.AttendanceRow
import com.example.attendance_app_2.models.Filter
import com.example.attendance_app_2.models.Subject

object ReportViewHelper {
    val TAG = this::class.java.simpleName
    fun extractSubjects(attendanceRow: AttendanceRow): List<Subject> {
        val subjects = mutableListOf<Subject>()
        for (subject in attendanceRow.percentages) {
            subjects.add(subject.first)
        }
        return subjects
    }

    fun populateTable(context: Context, tableLayout: TableLayout, attendanceReport: List<AttendanceRow>) {
        tableLayout.removeAllViews()
        if (attendanceReport.isEmpty()){
            Toast.makeText(context, "0 rows for the given filter", Toast.LENGTH_SHORT).show()
            return
        }
        tableLayout.addView(createHeader(context, attendanceReport[0]))

        var even = false;
        for (attRow in attendanceReport){
            val row = TableRow(context)
            row.addView(createTextView(context, attRow.roll))
            row.addView(createTextView(context, attRow.name))
            for (subject in attRow.percentages){
                row.addView(createTextView(context, subject.second.toString()))
            }
            if (even){
                row.setBackgroundResource(R.drawable.att_row_2)
            }else{
                row.setBackgroundResource(R.drawable.att_row_1)
            }
            even = !even
            tableLayout.addView(row)
        }
    }
    fun createHeader(context: Context,head: AttendanceRow): TableRow {
        val header = TableRow(context)
        header.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        header.setBackgroundResource(R.drawable.header_background)
        header.addView(createHeaderTextView(context,"ROLL NUMBER"))
        header.addView(createHeaderTextView(context,"NAME"))
        for (subject in head.percentages){
            header.addView(createHeaderTextView(context, subject.first.code))
        }
        return header
    }

    fun createTextView(context: Context, text: String): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        textView.minWidth = 200
        textView.setBackgroundResource(R.drawable.tv_background)
        return textView
    }


    private fun createHeaderTextView(context: Context, text: String): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        textView.setPadding(16, 16, 16, 16)
        textView.minWidth = 200
        textView.setBackgroundResource(R.drawable.tv_background)
        return textView
    }

    fun filterAttendance(context: Context, tableLayout: TableLayout, attendanceReport: List<AttendanceRow>, filter: Filter){
        var  filteredAttendance = emptyList<AttendanceRow>()
        try{
            val subjectIndex = attendanceReport[0].percentages.indexOfFirst { it.first.id == filter.subjectId }
            filteredAttendance = when (filter.symbol){
                "<=" -> attendanceReport.filter { it.percentages[subjectIndex].second <= filter.value }
                "<" -> attendanceReport.filter { it.percentages[subjectIndex].second < filter.value }
                ">" -> attendanceReport.filter { it.percentages[subjectIndex].second > filter.value }
                ">=" -> attendanceReport.filter { it.percentages[subjectIndex].second >= filter.value }
                else -> attendanceReport
            }
        }catch(e: Exception){
            Log.e(TAG, "filterAttendance: SQL error "+ e.message)
        }

        populateTable(context, tableLayout, filteredAttendance)
    }
}
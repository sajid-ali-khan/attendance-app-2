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
            subjects.add(subject.subject)
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
                row.addView(createTextView(context, subject.percentage.toString()))
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
        for (fieldName in getHeaderNames(head)){
            header.addView(createHeaderTextView(context, fieldName))
        }
        return header
    }

    fun getHeaderNames(firstRow: AttendanceRow): List<String> {
        val headerNames = mutableListOf<String>()
        headerNames.add("ROLL NUMBER")
        headerNames.add("NAME")
        for (subject in firstRow.percentages) {
            headerNames.add(subject.subject.code)
        }
        return headerNames
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
        textView.setTextColor(context.getColor(R.color.white))
        textView.setBackgroundResource(R.drawable.tv_background)
        return textView
    }

    fun filterAttendance(context: Context, tableLayout: TableLayout, attendanceReport: List<AttendanceRow>, filter: Filter): List<AttendanceRow>{
        var  filteredAttendance = emptyList<AttendanceRow>()
        try{
            val subjectIndex = attendanceReport[0].percentages.indexOfFirst { it.subject.id == filter.subjectId }
            filteredAttendance = when (filter.symbol){
                "<=" -> attendanceReport.filter { it.percentages[subjectIndex].percentage <= filter.value }
                "<" -> attendanceReport.filter { it.percentages[subjectIndex].percentage < filter.value }
                ">" -> attendanceReport.filter { it.percentages[subjectIndex].percentage > filter.value }
                ">=" -> attendanceReport.filter { it.percentages[subjectIndex].percentage >= filter.value }
                else -> attendanceReport
            }
        }catch(e: Exception){
            Log.e(TAG, "filterAttendance: SQL error "+ e.message)
        }
        return filteredAttendance
    }
}
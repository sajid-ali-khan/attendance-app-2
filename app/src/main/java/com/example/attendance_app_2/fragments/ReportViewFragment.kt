package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentReportViewBinding
import com.example.attendance_app_2.models.AttendanceRow
import kotlin.reflect.typeOf

class ReportViewFragment : Fragment(R.layout.fragment_report_view) {
    private lateinit var binding: FragmentReportViewBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReportViewBinding.bind(view)
        var attendanceReport: List<AttendanceRow> = emptyList()
        arguments?.let {
            attendanceReport = it.getParcelableArrayList("attendanceReport") ?: emptyList()
        }

        if (attendanceReport.isEmpty()){
            Toast.makeText(requireContext(), "Unable to fetch the attendance report!!", Toast.LENGTH_SHORT).show();
            return
        }

        val tableLayout = binding.attTable
        populateTable(tableLayout, attendanceReport)

    }

    private fun populateTable(table: TableLayout, attendanceReport: List<AttendanceRow>) {
        table.addView(createHeader(attendanceReport[0]))

        var even = false;
        for (attRow in attendanceReport){
            val row = TableRow(requireContext())
            row.addView(createTextView(attRow.roll))
            row.addView(createTextView(attRow.name))
            for (subject in attRow.percentages){
                row.addView(createTextView(subject.second.toString()))
            }
            if (even){
                row.setBackgroundResource(R.drawable.att_row_2)
            }else{
                row.setBackgroundResource(R.drawable.att_row_1)
            }
            even = !even
            table.addView(row)
        }
    }

    private fun createHeader(head: AttendanceRow): TableRow {
        val header = TableRow(requireContext())
        header.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        header.setBackgroundResource(R.drawable.header_background)
        header.addView(createHeaderTextView("ROLL NUMBER"))
        header.addView(createHeaderTextView("NAME"))
        for (subject in head.percentages){
            header.addView(createHeaderTextView(subject.first.code))
        }
        return header
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        textView.minWidth = 200;
        textView.setBackgroundResource(R.drawable.tv_background)
        return textView
    }
    private fun createHeaderTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        textView.setPadding(16, 24, 16, 24)
        textView.minWidth = 200
        textView.setBackgroundResource(R.drawable.tv_background)
        return textView
    }
}
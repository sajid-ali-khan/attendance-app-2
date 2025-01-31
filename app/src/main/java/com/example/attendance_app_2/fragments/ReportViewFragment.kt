package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentReportViewBinding
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.createHeader
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.createTextView
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.extractSubjects
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.filterAttendance
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.populateTable
import com.example.attendance_app_2.models.AttendanceRow
import com.example.attendance_app_2.models.Filter
import com.example.attendance_app_2.models.Subject

class ReportViewFragment : Fragment(R.layout.fragment_report_view) {
    private lateinit var binding: FragmentReportViewBinding
    private lateinit var spSymbol: Spinner
    private lateinit var spValue: Spinner
    private lateinit var spColumn: Spinner
    private lateinit var tableLayout: TableLayout

    var attendanceReport = emptyList<AttendanceRow>()

    private lateinit var selectedSymbol: String
    var selectedValue = 100
    var selectedSubject = 0

    var subs = emptyList<Subject>()

    val symbols = listOf("<=", "<", ">", ">=")
    val values = listOf(100, 75, 65, 40)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReportViewBinding.bind(view)

        spSymbol = binding.spSymbol
        spValue = binding.spValue
        spColumn = binding.spColumn

        selectedSymbol = symbols[0]

        populateSpinner(symbols, spSymbol)
        populateSpinner(values.map { it.toString() }, spValue)

        arguments?.let {
            attendanceReport = it.getParcelableArrayList("attendanceReport") ?: emptyList()
        }

        if (attendanceReport.isEmpty()){
            Toast.makeText(requireContext(), "Unable to fetch the attendance report!!", Toast.LENGTH_SHORT).show();
            return
        }

        subs = extractSubjects(attendanceReport[0])
        subs = subs.reversed()
        populateSpinner(subs.map { it.code }, spColumn)

        tableLayout = binding.attTable
        populateTable(requireContext(), tableLayout, attendanceReport)

        setUpFilterListeners();

    }

    private fun setUpFilterListeners(){
        spSymbol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSymbol = symbols[position]
                filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedValue = values[position]
                filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spColumn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSubject = position
                filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    private fun populateSpinner(items: List<String>, sp: Spinner){
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp.adapter = adapter
    }
}
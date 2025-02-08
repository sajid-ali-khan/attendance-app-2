package com.example.attendance_app_2.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentReportViewBinding
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.extractSubjects
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.filterAttendance
import com.example.attendance_app_2.fragments.helpers.ReportViewHelper.getHeaderNames
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
    var filteredAttendance = emptyList<AttendanceRow>()

    private lateinit var selectedSymbol: String
    var selectedValue = 100
    var selectedSubject = 0

    var subs = emptyList<Subject>()

    val symbols = listOf("<=", "<", ">", ">=")
    val values = listOf(100, 75, 65, 40)

    val CREATE_DOCUMENT_CODE = 51
    var filtered = false

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

        if (attendanceReport.isEmpty()) {
            binding.attContainer.visibility = View.GONE
            Toast.makeText(requireContext(), "No attendance data available.", Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.attContainer.visibility = View.VISIBLE
        }


        subs = extractSubjects(attendanceReport[0])
        subs = subs.reversed()
        populateSpinner(subs.map { it.code }, spColumn)

        tableLayout = binding.attTable
        populateTable(requireContext(), tableLayout, attendanceReport)

        setUpFilterListeners();

        binding.btnSave.setOnClickListener {
            exportAsCsv()
        }

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
                filteredAttendance = filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
                filtered = true
                populateTable(requireContext(), tableLayout , filteredAttendance)
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
                filteredAttendance = filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
                filtered = true
                populateTable(requireContext(), tableLayout , filteredAttendance)
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
                filteredAttendance = filterAttendance(requireContext(), tableLayout, attendanceReport, Filter(selectedSymbol, selectedValue, subs[selectedSubject].id))
                filtered = true
                populateTable(requireContext(), tableLayout , filteredAttendance)
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

    private fun exportAsCsv(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "AttendanceReport.csv")
        }
        startActivityForResult(intent, CREATE_DOCUMENT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_DOCUMENT_CODE && resultCode == RESULT_OK){
            data?.data?.let {uri ->
                if(writeToFile(uri)){
                    Toast.makeText(requireContext(), "File saved successfully", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Failed to save the file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun writeToFile(uri: Uri): Boolean{
        return try{
            requireContext().contentResolver.openOutputStream(uri)?.use{outputStream ->
                var content = ""
                if (!filtered){
                    content = convertToCsv(attendanceReport)
                }else{
                    content = convertToCsv(filteredAttendance)
                }
                Log.d("Content is..", "writeToFile: ${content}")
                outputStream.write(content.toByteArray())
            }
            true
        }catch(e: Exception){
            Log.e(this::class.simpleName, "writeToFile: File operation error", e)
            false
        }
    }

    private fun convertToCsv(givenReport: List<AttendanceRow>): String {
        val csvData = StringBuilder()
        if (givenReport.isEmpty()){
            csvData.append(getHeaderNames(attendanceReport[0]).joinToString(","))
            csvData.append("\n")
            return csvData.toString()
        }
        csvData.append(getHeaderNames(givenReport[0]).joinToString(","))
        csvData.append("\n")
        for (row in givenReport) {
            val rowList = mutableListOf<String>()
            rowList.add(row.roll)
            rowList.add(row.name)
            for (subject in row.percentages) {
                rowList.add(subject.percentage.toString())
            }
            csvData.append(rowList.joinToString(","))
            csvData.append("\n")
        }
        return csvData.toString()
    }

}
package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentAllAttReportBinding
import com.example.attendance_app_2.db.AttendanceReportHelper.generateAllAttendanceReport
import com.example.attendance_app_2.db.DataFetcher
import com.example.attendance_app_2.db.DatesHelper
import com.example.attendance_app_2.db.SeeAssignmentsHelper.fetchAssignmentIds
import com.example.attendance_app_2.db.UpdateAttendanceHelper.validDate
import com.example.attendance_app_2.fragments.ReportViewFragment
import com.example.attendance_app_2.models.SemesterDates
import com.example.attendance_app_2.utils.BranchYearMapper
import com.example.attendance_app_2.utils.CustomSpinnerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomAttendanceReportFragment : Fragment(R.layout.fragment_all_att_report) {

    val TAG = this::class.java.simpleName
    private lateinit var binding: FragmentAllAttReportBinding

    private lateinit var spBranch: Spinner
    private lateinit var spSemester: Spinner
    private lateinit var spSection: Spinner
    private lateinit var etFromDate: TextView
    private lateinit var etToDate: TextView

    lateinit var branchAdapter: CustomSpinnerAdapter
    lateinit var semesterAdapter: CustomSpinnerAdapter
    lateinit var sectionAdapter: CustomSpinnerAdapter

    lateinit var branches: MutableList<Int>
    lateinit var semesters: MutableList<Int>
    lateinit var sections: MutableList<String>

    var selectedBranch: String = ""
    var selectedSemester: String = ""
    var selectedSection: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllAttReportBinding.bind(view)

        spBranch = binding.spBranch
        spSemester = binding.spSem
        spSection = binding.spSection
        etFromDate = binding.etFromDate
        etToDate = binding.etToDate

        setDates();

        initializeSpinnerAdapters()
        setUpSpinnerListeners()

        binding.btnGenerateReport.setOnClickListener{
            if (notAllSelected()) return@setOnClickListener

            lifecycleScope.launch {
                val assignmentIds = fetchAssignmentIds(selectedBranch, selectedSemester, selectedSection)
                val startDate = etFromDate.text.toString()
                val endDate = etToDate.text.toString()

                if (assignmentIds.isEmpty()) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "No assignments found", Toast.LENGTH_SHORT).show()
                    }
                }else if (!validDate(startDate) || !validDate(endDate)){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "Invalid dates", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    val attendanceReport = generateAllAttendanceReport(requireContext(), assignmentIds, SemesterDates(startDate, endDate))
                    Log.d(TAG, "fetched Attendance Reprot: $attendanceReport")
                    withContext(Dispatchers.Main){
                        val reportViewFragment = ReportViewFragment()
                        reportViewFragment.arguments = Bundle().apply {
                            putParcelableArrayList("attendanceReport", ArrayList(attendanceReport))
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, reportViewFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }


            }
        }
    }

    private fun setDates() {
        lifecycleScope.launch {
            val dates = DatesHelper.fetchDates()
            withContext(Dispatchers.Main){
                binding.etFromDate.setText(dates.startDate)
                binding.etToDate.setText(dates.endDate)
            }
        }
    }


    private fun initializeSpinner(spinner: Spinner, title: String): CustomSpinnerAdapter {
        val adapter = CustomSpinnerAdapter(requireContext(), title, mutableListOf())
        spinner.adapter = adapter
        return adapter
    }

    private fun initializeSpinnerAdapters() {
        branchAdapter = initializeSpinner(spBranch, getString(R.string.select_branch))
        semesterAdapter = initializeSpinner(spSemester, getString(R.string.select_semester))
        sectionAdapter = initializeSpinner(spSection, getString(R.string.select_section))
        updateBranches()
    }

    private fun updateBranches() {
        lifecycleScope.launch {
            branches = DataFetcher.fetchAllBranches(requireContext())
            if (branches.isEmpty()) {
                Log.d(TAG, "populateBranches: Branches is empty")
            }
            withContext(Dispatchers.Main) {
                branchAdapter.updateItems(branches.map { BranchYearMapper.getBranchName(it) }
                    .toMutableList())
            }
        }
    }

    private fun updateSemesters() {
        lifecycleScope.launch {
            semesters = DataFetcher.fetchSemesters(requireContext(), selectedBranch)
            if (semesters.isEmpty()) {
                Log.d(TAG, "populateBranches: Branches is empty")
            }
            withContext(Dispatchers.Main) {
                semesterAdapter.updateItems(semesters.map { it.toString() }
                    .toMutableList())
            }
        }
    }

    private fun updateSections() {
        lifecycleScope.launch {
            sections = DataFetcher.fetchSections(requireContext(), selectedBranch, selectedSemester)
            if (sections.isEmpty()) {
                Log.d(TAG, "populateSections: Section is empty")
            }
            withContext(Dispatchers.Main) {
                sectionAdapter.updateItems(sections)
            }
        }
    }

    private fun setUpSpinnerListeners() {
        spBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedBranch = branches[position - 1].toString()
                    Log.d(TAG, "Selected Branch: $selectedBranch")
                } else {
                    selectedBranch = ""
                    Log.d(TAG, "No branch selected")
                }
                updateSemesters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "No branch selected")
            }
        }

        spSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedSemester = semesters[position - 1].toString()
                    Log.d(TAG, "onItemSelected: selected semester: $selectedSemester")
                } else {
                    selectedSemester = ""
                    Log.d(TAG, "No semester selected")
                }
                updateSections()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spSection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedSection = sections[position - 1]
                    Log.d(TAG, "onItemSelected: selected section: $selectedSection")
                } else {
                    selectedSection = ""
                    Log.d(TAG, "No section selected")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun notAllSelected(): Boolean {
        val missingFields = mutableListOf<String>()
        if (selectedBranch.isEmpty()) missingFields.add("Branch")
        if (selectedSemester.isEmpty()) missingFields.add("Semester")
        if (selectedSection.isEmpty()) missingFields.add("Section")

        if (missingFields.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please select: ${missingFields.joinToString(", ")}",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }
}
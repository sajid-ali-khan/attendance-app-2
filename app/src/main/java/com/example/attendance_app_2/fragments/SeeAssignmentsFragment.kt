package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.AssignmentAdapter
import com.example.attendance_app_2.databinding.FragmentSeeAssignmentsBinding
import com.example.attendance_app_2.db.DataFetcher
import com.example.attendance_app_2.db.SeeAssignmentsHelper
import com.example.attendance_app_2.models.Assignment
import com.example.attendance_app_2.utils.BranchYearMapper
import com.example.attendance_app_2.utils.CustomSpinnerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeeAssignmentsFragment : Fragment(R.layout.fragment_see_assignments) {
    private lateinit var binding : FragmentSeeAssignmentsBinding

    lateinit var schemeAdapter: CustomSpinnerAdapter
    lateinit var branchAdapter: CustomSpinnerAdapter
    lateinit var semesterAdapter: CustomSpinnerAdapter
    lateinit var sectionAdapter: CustomSpinnerAdapter

    lateinit var schemes: MutableList<String>
    lateinit var branches: MutableList<Int>
    lateinit var semesters: MutableList<Int>
    lateinit var sections: MutableList<String>

    lateinit var spScheme : Spinner
    lateinit var spBranch : Spinner
    lateinit var spSemester : Spinner
    lateinit var spSection : Spinner

    var selectedScheme: String = ""
    var selectedBranch: String = ""
    var selectedSemester: String = ""
    var selectedSection: String = ""

    val TAG = this::class.java.simpleName

    private lateinit var assignmentAdapter: AssignmentAdapter
    private lateinit var assignments: List<Assignment>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSeeAssignmentsBinding.bind(view)

        spScheme = binding.spScheme
        spBranch = binding.spBranch
        spSemester = binding.spSemester
        spSection = binding.spSection

        initializeSpinnerAdapters()
        setUpSpinnerListeners()

        binding.btnFetchAssignments.setOnClickListener{
            if (notAllSelected()) return@setOnClickListener

            lifecycleScope.launch {
                val assignments = SeeAssignmentsHelper.fetchAssignments(requireContext(), selectedScheme, selectedBranch, selectedSemester, selectedSection)
                if (assignments.isEmpty()){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "No assignments found", Toast.LENGTH_SHORT).show()
                        binding.tvClassName.text = ""
                    }
                }else{
                    val className = SeeAssignmentsHelper.formClassName(selectedBranch, selectedSemester, selectedSection)
                    withContext(Dispatchers.Main) {
                        binding.tvClassName.text = className
                    }
                }

                withContext(Dispatchers.Main) {
                    showAssignments(assignments)
                }
            }
        }
    }

    private fun initializeSpinner(spinner: Spinner, title: String): CustomSpinnerAdapter {
        val adapter = CustomSpinnerAdapter(requireContext(), title, mutableListOf())
        spinner.adapter = adapter
        return adapter
    }

    private fun initializeSpinnerAdapters() {
        schemeAdapter = initializeSpinner(spScheme, getString(R.string.select_scheme))
        branchAdapter = initializeSpinner(spBranch, getString(R.string.select_branch))
        semesterAdapter = initializeSpinner(spSemester, getString(R.string.select_semester))
        sectionAdapter = initializeSpinner(spSection, getString(R.string.select_section))
        updateSchemes()
    }

    private fun updateSchemes() {
        lifecycleScope.launch {
            schemes = DataFetcher.fetchSchemes(requireContext())
            if (schemes.isEmpty()) {
                Log.d(TAG, "populateSchemes: Schemes is empty")
            }
            withContext(Dispatchers.Main) {
                schemeAdapter.updateItems(schemes)
            }
        }
    }

    private fun updateBranches() {
        lifecycleScope.launch {
            branches = DataFetcher.fetchBranches(requireContext(), selectedScheme)
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
            semesters = DataFetcher.fetchSemesters(requireContext(), selectedScheme, selectedBranch)
            if (semesters.isEmpty()) {
                Log.d(TAG, "populateSemesters: Semesters is empty")
            }
            withContext(Dispatchers.Main) {
                semesterAdapter.updateItems(semesters.map { it.toString() }.toMutableList())
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
        spScheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedScheme = schemes[position - 1]
                    Log.d(TAG, "Selected Scheme: $selectedScheme")
                    updateBranches()
                } else {
                    selectedScheme = ""
                    Log.d(TAG, "No scheme selected")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "No scheme selected")
            }
        }

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
                    updateSemesters()
                } else {
                    selectedBranch = ""
                    Log.d(TAG, "No branch selected")
                }
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
                    updateSections()
                } else {
                    selectedSemester = ""
                    Log.d(TAG, "No semester selected")
                }
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
        if (selectedScheme.isEmpty()) missingFields.add("Scheme")
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


    private fun showAssignments(assignments: List<Assignment>) {
        val recyclerView: RecyclerView = binding.rvAssignments

        assignmentAdapter = AssignmentAdapter(assignments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = assignmentAdapter
    }

}
package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentAssignClassBinding
import com.example.attendance_app_2.db.BranchYearMapper
import com.example.attendance_app_2.models.Course
import com.example.attendance_app_2.db.DataFetcher
import com.example.attendance_app_2.utils.CustomSpinnerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignClassFragment : Fragment(R.layout.fragment_assign_class) {

    val TAG = "AssignClassFragment"
    lateinit var binding: FragmentAssignClassBinding

    lateinit var schemeAdapter: CustomSpinnerAdapter
    lateinit var branchAdapter: CustomSpinnerAdapter
    lateinit var semesterAdapter: CustomSpinnerAdapter
    lateinit var sectionAdapter: CustomSpinnerAdapter
    lateinit var subjectAdapter: CustomSpinnerAdapter
    lateinit var facultyAdapter: CustomSpinnerAdapter

    lateinit var spSchemes: Spinner
    lateinit var spBranch: Spinner
    lateinit var spSemester: Spinner
    lateinit var spSection: Spinner
    lateinit var spSubject: Spinner
    lateinit var autoTvFaculty: AutoCompleteTextView

    lateinit var schemes: MutableList<String>
    lateinit var branches: MutableList<Int>
    lateinit var semesters: MutableList<Int>
    lateinit var sections: MutableList<String>
    lateinit var subjects: MutableList<Course>

    lateinit var selectedScheme: String
    lateinit var selectedBranch: String
    lateinit var selectedSemester: String
    lateinit var selectedSection: String
    lateinit var selectedCourse: String
    lateinit var selectedFaculty: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAssignClassBinding.bind(view)

        spSchemes = binding.spScheme
        spBranch = binding.spBranch
        spSemester = binding.spSemester
        spSection = binding.spSection
        spSubject = binding.spSubject
        autoTvFaculty = binding.autoTvFaculty

        initializeSpinnerAdapters()
        populateSchemes()
        setUpSpinnerListeners()



        binding.btnAssignClass.setOnClickListener {
            //TODO: Look if all fields are filled

            //TODO: Check if any other faculty is already assigned to this same class(through course id, section)

            //TODO: Assign the class to the faculty via inserting the assignment to the assignments table
            Toast.makeText(requireContext(), "Class Assigned", Toast.LENGTH_SHORT).show()
        }


    }

    private fun setUpSpinnerListeners() {
        spSchemes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedScheme = schemes[position-1]
                    Log.d(TAG, "Selected Scheme: $selectedScheme")
                    populateBranches()
                } else {
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
                    selectedBranch = branches[position-1].toString()
                    Log.d(TAG, "Selected Branch: $selectedBranch")
                    populateSemesters()
                } else {
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
                if (position != 0){
                    selectedSemester = semesters[position-1].toString()
                    Log.d(TAG, "onItemSelected: selected semester: $selectedSemester")
                    populateSections()
                }else{
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
                if (position != 0){
                    selectedSection = sections[position-1]
                    Log.d(TAG, "onItemSelected: selected section: $selectedSection")
                    populateSubjects()
                }else{
                    Log.d(TAG, "No section selected")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        spSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0){
                    Log.d(TAG, "onItemSelected: selected course = ${subjects[position - 1]}")
                }else{
                    Log.d(TAG, "No course selected")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


    private fun initializeSpinnerAdapters() {
        schemeAdapter = CustomSpinnerAdapter(requireContext(), "Select Scheme", mutableListOf())
        spSchemes.adapter = schemeAdapter
        branchAdapter = CustomSpinnerAdapter(requireContext(), "Select Branch", mutableListOf())
        spBranch.adapter = branchAdapter
        semesterAdapter =
            CustomSpinnerAdapter(requireContext(), "Select Semester", mutableListOf())
        spSemester.adapter = semesterAdapter
        subjectAdapter =
            CustomSpinnerAdapter(requireContext(), "Select Subject", mutableListOf())
        spSubject.adapter = subjectAdapter
        sectionAdapter =
            CustomSpinnerAdapter(requireContext(), "Select Section", mutableListOf())
        spSection.adapter = sectionAdapter
    }

    private fun populateSchemes() {
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

    private fun populateBranches() {
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

    private fun populateSemesters() {
        lifecycleScope.launch {
            semesters = DataFetcher.fetchSemesters(requireContext(), selectedScheme, selectedBranch)
            if (semesters.isEmpty()) {
                Log.d(TAG, "populateSemesters: Semesters is empty")
            }
            withContext(Dispatchers.Main){
                semesterAdapter.updateItems(semesters.map { it.toString() }.toMutableList())
            }
        }
    }

    private fun populateSections() {
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

    private fun populateSubjects() {
        lifecycleScope.launch {
            subjects = DataFetcher.fetchSubjects(requireContext(), selectedScheme, selectedBranch, selectedSemester)
            if (subjects.isEmpty()) {
                Log.d(TAG, "populateSubjects: Subjects is empty")
            }
            withContext(Dispatchers.Main) {
                subjectAdapter.updateItems(subjects.map { "${it.scode}(${it.subname})" }.toMutableList())
            }
        }
    }
}
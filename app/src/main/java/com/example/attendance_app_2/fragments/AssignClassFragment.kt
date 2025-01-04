package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentAssignClassBinding
import com.example.attendance_app_2.db.AssignClassHelper
import com.example.attendance_app_2.db.DataFetcher
import com.example.attendance_app_2.models.Course
import com.example.attendance_app_2.models.Faculty
import com.example.attendance_app_2.utils.BranchYearMapper
import com.example.attendance_app_2.utils.CustomSpinnerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignClassFragment : Fragment(R.layout.fragment_assign_class) {

    val TAG = "AssignClassFragment"
    lateinit var binding: FragmentAssignClassBinding

    //spinner adapters
    lateinit var schemeAdapter: CustomSpinnerAdapter
    lateinit var branchAdapter: CustomSpinnerAdapter
    lateinit var semesterAdapter: CustomSpinnerAdapter
    lateinit var sectionAdapter: CustomSpinnerAdapter
    lateinit var subjectAdapter: CustomSpinnerAdapter

    //spinners
    lateinit var spSchemes: Spinner
    lateinit var spBranch: Spinner
    lateinit var spSemester: Spinner
    lateinit var spSection: Spinner
    lateinit var spSubject: Spinner
    lateinit var autoTvFaculty: AutoCompleteTextView

    //data lists
    lateinit var schemes: MutableList<String>
    lateinit var branches: MutableList<Int>
    lateinit var semesters: MutableList<Int>
    lateinit var sections: MutableList<String>
    lateinit var subjects: MutableList<Course>
    lateinit var faculties: MutableList<Faculty>

    //selected items
    var selectedScheme: String = ""
    var selectedBranch: String = ""
    var selectedSemester: String = ""
    var selectedSection: String = ""
    var selectedCourse: String = ""
    var selectedFaculty: String = ""

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
        setUpSpinnerListeners()
        setUpFaculties()


        binding.btnAssignClass.setOnClickListener {
            if (notAllSelected()){
                Toast.makeText(requireContext(), "Please select all input fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(isInvalidFaculty()){
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val alreadyAssigned = AssignClassHelper.doesAssignmentAlreadyExists(requireContext(), selectedFaculty, selectedCourse, selectedSection)

                if (alreadyAssigned[0] as Boolean) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Class already assigned", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val result = AssignClassHelper.assignClass(
                    requireContext(),
                    selectedFaculty,
                    selectedCourse,
                    selectedSection)

                withContext(Dispatchers.Main){
                    if (result) {
                        Toast.makeText(requireContext(), "Class assigned", Toast.LENGTH_SHORT).show()
                        updateSubjects()
                        autoTvFaculty.setText("")
                    } else {
                        Toast.makeText(requireContext(), "Class not assigned", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    private fun setUpFaculties() {
        lifecycleScope.launch {
            faculties = DataFetcher.fetchFaculties()
            if (faculties.isEmpty()) {
                Log.d(TAG, "setUpFaculties: Faculties is empty")
            }
            withContext(Dispatchers.Main) {
                setUpAutoTvFaculty(faculties)
            }
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
                    updateSubjects()
                } else {
                    selectedSection = ""
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
                if (position != 0) {
                    selectedCourse = subjects[position - 1].id.toString()
                    Log.d(TAG, "onItemSelected: selected course = ${subjects[position - 1]}")
                } else {
                    selectedCourse = ""
                    Log.d(TAG, "No course selected")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        autoTvFaculty.setOnItemClickListener { parent, view, position, id ->
            selectedFaculty = parent.getItemAtPosition(position) as String
            selectedFaculty = selectedFaculty.split("-")[0].trim()
            Log.d(TAG, "setUpSpinnerListeners: selected faculty ${selectedFaculty}")
        }
    }


    private fun initializeSpinnerAdapters() {
        schemeAdapter = CustomSpinnerAdapter(requireContext(), "Select Scheme", mutableListOf())
        spSchemes.adapter = schemeAdapter

        branchAdapter = CustomSpinnerAdapter(requireContext(), "Select Branch", mutableListOf())
        spBranch.adapter = branchAdapter

        semesterAdapter = CustomSpinnerAdapter(requireContext(), "Select Semester", mutableListOf())
        spSemester.adapter = semesterAdapter

        subjectAdapter = CustomSpinnerAdapter(requireContext(), "Select Subject", mutableListOf())
        spSubject.adapter = subjectAdapter

        sectionAdapter = CustomSpinnerAdapter(requireContext(), "Select Section", mutableListOf())
        spSection.adapter = sectionAdapter

        updateSchemes() //starting point
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

    private fun updateSubjects() {
        lifecycleScope.launch {
            subjects = DataFetcher.fetchSubjects(
                requireContext(),
                selectedScheme,
                selectedBranch,
                selectedSemester
            )
            if (subjects.isEmpty()) {
                Log.d(TAG, "populateSubjects: Subjects is empty")
            }
            withContext(Dispatchers.Main) {
                subjectAdapter.updateItems(subjects.map { "${it.scode}(${it.subname})" }
                    .toMutableList())
            }
        }
    }

    private fun setUpAutoTvFaculty(facultyList: MutableList<Faculty>) {
        val facultyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            facultyList.map { "${it.id} - ${it.name}" }
        )
        autoTvFaculty.setAdapter(facultyAdapter)
    }

    private fun notAllSelected(): Boolean {
        return selectedScheme.isEmpty() || selectedBranch.isEmpty() || selectedSemester.isEmpty() || selectedSection.isEmpty() || selectedCourse.isEmpty()
    }

    private fun isInvalidFaculty(): Boolean {
        if(! faculties.any { it.id == selectedFaculty }){
            Toast.makeText(requireContext(), "Invalid Faculty", Toast.LENGTH_SHORT).show()
            return true
        }
        return false

    }

}
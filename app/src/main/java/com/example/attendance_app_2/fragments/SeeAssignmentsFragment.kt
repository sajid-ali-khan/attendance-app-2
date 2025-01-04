package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentSeeAssignmentsBinding
import com.example.attendance_app_2.utils.CustomSpinnerAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
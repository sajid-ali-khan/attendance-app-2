package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.attendance_app_2.databinding.FragmentPageHolderBinding

class DefaultAttReportFragment : Fragment() {
    lateinit var binding: FragmentPageHolderBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPageHolderBinding.bind(view)

        //get the empid of the user
        //fetch the related assignments
        //call the function which takes assignments id and give attendance of that student in (roll, percentage) format
    }
}
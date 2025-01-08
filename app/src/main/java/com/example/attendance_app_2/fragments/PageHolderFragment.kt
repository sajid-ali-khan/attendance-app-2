package com.example.attendance_app_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.attendance_app_2.R
import com.example.attendance_app_2.fragments.attendance_fragments.AllAttReportFragment
import com.example.attendance_app_2.fragments.attendance_fragments.DefaultAttReportFragment
import com.example.attendance_app_2.fragments.attendance_fragments.StudentAttReportFragment

class PageHolderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_page_holder, container, false)

        val fragmentList = arrayListOf<Fragment>(
            DefaultAttReportFragment(),
            AllAttReportFragment(),
            StudentAttReportFragment()
        )

        return view
    }
}
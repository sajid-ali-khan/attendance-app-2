package com.example.attendance_app_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.ViewPagerAdapter
import com.example.attendance_app_2.databinding.FragmentPageHolderBinding
import com.example.attendance_app_2.fragments.report_fragments.AllAttReportFragment
import com.example.attendance_app_2.fragments.report_fragments.DefaultAttReportFragment
import com.example.attendance_app_2.fragments.report_fragments.StudentAttReportFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PageHolderFragment : Fragment(R.layout.fragment_page_holder) {
    private lateinit var binding : FragmentPageHolderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPageHolderBinding.inflate(inflater, container, false)
        val view =  super.onCreateView(inflater, container, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(
            AllAttReportFragment(),
            DefaultAttReportFragment(),
            StudentAttReportFragment()
        )

        val adapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle,
            fragmentList
        )

        binding.viewPager.adapter = adapter

        return view;
    }
}
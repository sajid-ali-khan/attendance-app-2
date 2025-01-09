package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.example.attendance_app_2.R

class AllAttReportFragment : Fragment(R.layout.fragment_all_att_report) {

    val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Hello")
    }
}
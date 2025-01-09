package com.example.attendance_app_2.fragments.report_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View

class AllAttReportFragment : Fragment() {

    val TAG = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Hello")
    }
}
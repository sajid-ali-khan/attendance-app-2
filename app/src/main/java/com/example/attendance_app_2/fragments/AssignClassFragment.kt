package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.FragmentAssignClassBinding
import com.example.attendance_app_2.utils.CustomSpinnerAdapter

class AssignClassFragment : Fragment(R.layout.fragment_assign_class) {
    lateinit var binding: FragmentAssignClassBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAssignClassBinding.bind(view)

        //TODO: Populate the branch spinner
        val hint = "Select Class"
        val initialItems = mutableListOf("CST", "CSE", "ECE", "CSM", "EEE")

        val adapter = CustomSpinnerAdapter(requireContext(), hint, initialItems)
        binding.spBranch.adapter = adapter
        //TODO: Manage the auto complete textview
        //TODO: Manage the spinners

        binding.spBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position == 0){
                    //ignore
                }else{
                    val selectedClass = initialItems[position-1]
                    Toast.makeText(requireContext(), "Selected Class: $selectedClass", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.btnAssignClass.setOnClickListener{
            //TODO: Look if all fields are filled

            //TODO: Check if any other faculty is already assigned to this same class(through course id, section)

            //TODO: Assign the class to the faculty via inserting the assignment to the assignments table
            Toast.makeText(requireContext(), "Class Assigned", Toast.LENGTH_SHORT).show()
        }


    }
}
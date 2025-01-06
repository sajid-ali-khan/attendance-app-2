package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.models.Assignment
import com.example.attendance_app_2.R;
import com.example.attendance_app_2.databinding.ItemAssignmentBinding

class AssignmentAdapter(private val assignments: List<Assignment>) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val binding = ItemAssignmentBinding.bind(holder.itemView)
        val assignment = assignments[position]
        binding.tvScode.text = assignment.scode
        binding.tvSubname.text = assignment.subname
        val faculty = assignment.facultyName + " (" + assignment.facultyId + ")"
        binding.tvFaculty.text = faculty
    }

    override fun getItemCount(): Int = assignments.size
}

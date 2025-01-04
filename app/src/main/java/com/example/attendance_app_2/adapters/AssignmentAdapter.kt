package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.models.Assignment
import com.example.attendance_app_2.R;

class AssignmentAdapter(
    private val assignments: List<Assignment>
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scode: TextView = view.findViewById(R.id.tv_scode)
        val subname: TextView = view.findViewById(R.id.tv_subname)
        val facultyName: TextView = view.findViewById(R.id.tv_faculty_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]
        holder.scode.text = assignment.scode
        holder.subname.text = assignment.subname
        holder.facultyName.text = assignment.facultyName
    }

    override fun getItemCount(): Int = assignments.size
}

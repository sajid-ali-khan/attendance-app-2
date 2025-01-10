package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.models.AssignedSubject
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.ItemSubjectAttendanceBinding

class AttendanceListAdapter(
    private val assignedSubjects: List<AssignedSubject>,
    private val onSubjectClick: (String) -> Unit
): RecyclerView.Adapter<AttendanceListAdapter.AttendanceListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject_attendance, parent, false)
        return AttendanceListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AttendanceListViewHolder,
        position: Int
    ) {
        val binding: ItemSubjectAttendanceBinding = ItemSubjectAttendanceBinding.bind(holder.itemView)
        val assignedSubject = assignedSubjects[position]
        binding.tvSubjectCode.text = assignedSubject.subjectCode
        binding.tvClassName.text = assignedSubject.className

        binding.root.setOnClickListener{
            onSubjectClick(assignedSubject.assignmentId)
        }
    }

    override fun getItemCount(): Int {
        return assignedSubjects.size
    }


    inner class AttendanceListViewHolder(view: View): RecyclerView.ViewHolder(view)
}
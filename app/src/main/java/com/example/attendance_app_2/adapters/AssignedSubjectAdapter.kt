package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.ItemAssignedSubjectBinding
import com.example.attendance_app_2.models.AssignedSubject

class AssignedSubjectAdapter(
    private val classList: List<AssignedSubject>,
    private val onClassClick: (String) -> Unit // Callback for navigation
) : RecyclerView.Adapter<AssignedSubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_assigned_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val binding: ItemAssignedSubjectBinding = ItemAssignedSubjectBinding.bind(holder.itemView)
        val classItem = classList[position]
        binding.tvSubjectName.text = classItem.subjectCode
        binding.tvSubjectAbbreviation.text = classItem.className

        // Set button click listener
        binding.btnMark.setOnClickListener {
            onClassClick(classItem.assignmentId) // Trigger navigation callback
        }
    }

    override fun getItemCount(): Int = classList.size
}

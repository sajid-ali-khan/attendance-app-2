package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.ItemStudentBinding
import com.example.attendance_app_2.models.GenericStudent


class GenericStudentAdapter(
    private val studentsList: MutableList<GenericStudent>
) : RecyclerView.Adapter<GenericStudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun getItemCount(): Int = studentsList.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val binding = ItemStudentBinding.bind(holder.itemView)
        val student = studentsList[position]

        binding.tvStudentName.text = student.name
        binding.tvRollNumber.text = student.roll

        binding.tvStatus.setText(
            if (student.attStatus) R.string.present else R.string.absent
        )

        binding.root.setBackgroundColor(
            holder.itemView.context.getColor(
                if (student.attStatus) R.color.green else R.color.light_grey
            )
        )

        binding.root.setOnClickListener {
            student.attStatus = !student.attStatus
            notifyItemChanged(position)
        }
    }

    fun getUpdatedStudentList(): List<GenericStudent> = studentsList

    fun markAllAbsent() {
        for (i in studentsList.indices) {
            studentsList[i].attStatus = false
            notifyItemChanged(i)
        }
    }

    fun markAllPresent() {
        for (i in studentsList.indices) {
            studentsList[i].attStatus = true
            notifyItemChanged(i)
        }
    }
}

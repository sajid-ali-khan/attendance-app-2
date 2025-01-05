package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.databinding.ItemStudentBinding
import com.example.attendance_app_2.models.Student
import com.example.attendance_app_2.R

class StudentAdapter(
    private val studentList: MutableList<Student> // MutableList to allow modifications
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StudentViewHolder,
        position: Int
    ) {
        val binding: ItemStudentBinding = ItemStudentBinding.bind(holder.itemView)
        val student = studentList[position]

        binding.tvStudentName.text = student.name
        binding.tvRollNumber.text = student.roll

        binding.icAttStatus.setImageResource(
            if (student.attStatus) R.drawable.icon_checked else R.drawable.icon_unchecked
        )

        binding.root.setOnClickListener {
            student.attStatus = !student.attStatus

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun getUpdatedStudentList(): List<Student> {
        return studentList
    }

    fun markAllAbsent() {
        for ( i in 0 until studentList.size){
            studentList[i].attStatus = false
            notifyItemChanged(i)
        }
    }

    fun markAllPresent() {
        for ( i in 0 until studentList.size){
            studentList[i].attStatus = true
            notifyItemChanged(i)
        }
    }
}

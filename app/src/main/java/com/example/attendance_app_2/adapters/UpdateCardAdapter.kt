package com.example.attendance_app_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance_app_2.R
import com.example.attendance_app_2.databinding.ItemUpdateAttendanceCardBinding
import com.example.attendance_app_2.models.UpdateCard

class UpdateCardAdapter(
    private val sessionsList: List<UpdateCard>,
    private val onUpdateClick: (Int) -> Unit
): RecyclerView.Adapter<UpdateCardAdapter.UpdateCardViewHolder>() {
    inner class UpdateCardViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_update_attendance_card, parent, false)
        return UpdateCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessionsList.size
    }

    override fun onBindViewHolder(holder: UpdateCardViewHolder, position: Int) {
        val binding = ItemUpdateAttendanceCardBinding.bind(holder.itemView)
        val sessionItem = sessionsList[position]
        binding.tvScode.text = sessionItem.scode
        binding.tvClassName.text = sessionItem.className
        binding.tvTimestamp.text = sessionItem.timestamp
        binding.tvNumPA.text = sessionItem.numPA

        binding.btnUpdate.setOnClickListener {
            onUpdateClick(sessionItem.sessionId)
        }
    }
}
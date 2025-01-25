package com.example.attendance_app_2.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendance_app_2.R
import com.example.attendance_app_2.adapters.UpdateCardAdapter
import com.example.attendance_app_2.databinding.FragmentUpdateAttendanceBinding
import com.example.attendance_app_2.db.UpdateAttendanceHelper.fetchSessions
import com.example.attendance_app_2.db.UpdateAttendanceHelper.validDate
import com.example.attendance_app_2.models.UpdateCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateAttendanceFragment : Fragment(R.layout.fragment_update_attendance) {
    private lateinit var bindings : FragmentUpdateAttendanceBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings = FragmentUpdateAttendanceBinding.bind(view)

        bindings.btnFetchSessions.setOnClickListener {
            val date = bindings.etDate.text.toString()

            if (!validDate(date)){
                Toast.makeText(requireContext(), "Invalid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val sessions = fetchSessions(requireContext(), date)
                if (sessions.isEmpty()){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "No sessions on the specified date.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    withContext(Dispatchers.Main){
                        displaySessions(sessions);
                    }
                }
            }
        }
    }

    private fun displaySessions(sessions: List<UpdateCard>) {
        val adapter = UpdateCardAdapter(sessions){sessionId ->
            onSubjectClicked(sessionId)
        }
        bindings.rvSessionContainer.adapter = adapter
        bindings.rvSessionContainer.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onSubjectClicked(sessionId: Int) {
        Toast.makeText(requireContext(), "Session Id: $sessionId", Toast.LENGTH_SHORT).show()
    }
}
package com.example.attendance_app_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendance_app_2.databinding.ActivityDashboardBinding
import com.example.attendance_app_2.fragments.AssignClassFragment
import com.example.attendance_app_2.fragments.AttendanceReportFragment
import com.example.attendance_app_2.fragments.HomeFragment
import com.example.attendance_app_2.fragments.MarkAttendanceFragment
import com.example.attendance_app_2.fragments.SeeAssignmentsFragment
import com.example.attendance_app_2.fragments.UpdateAttendanceFragment

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val homeFragment = HomeFragment()
        val assignClassFragment = AssignClassFragment()
        val attendanceReportFragment = AttendanceReportFragment()
        val markAttendanceFragment = MarkAttendanceFragment()
        val seeAssignmentsFragment = SeeAssignmentsFragment()
        val updateAttendanceFragment = UpdateAttendanceFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(binding.container.id, homeFragment).commit()
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.miHome -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, homeFragment).commit()
                    }
                }
                R.id.miAssignClass -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, assignClassFragment).commit()
                    }
                }
                R.id.miAttendanceReport -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, attendanceReportFragment).commit()
                    }
                }
                R.id.miMarkAttendance -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, markAttendanceFragment).commit()
                    }
                }
                R.id.miSeeAssignments -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, seeAssignmentsFragment).commit()
                    }
                }
                R.id.miUpdateAttendance -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, updateAttendanceFragment).commit()
                    }
                }
                R.id.miLogout -> {
                    //logout logic
                }
            }
            true
        }
    }
}
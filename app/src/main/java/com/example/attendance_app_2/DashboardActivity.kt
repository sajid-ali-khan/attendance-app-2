package com.example.attendance_app_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendance_app_2.databinding.ActivityDashboardBinding
import com.example.attendance_app_2.fragments.AssignClassFragment
import com.example.attendance_app_2.fragments.HomeFragment
import com.example.attendance_app_2.fragments.MarkAttendanceFragment
import com.example.attendance_app_2.fragments.SeeAssignmentsFragment
import com.example.attendance_app_2.fragments.SemesterDatesFragment
import com.example.attendance_app_2.fragments.UpdateAttendanceFragment
import com.example.attendance_app_2.fragments.report_fragments.CustomAttendanceReportFragment
import com.example.attendance_app_2.fragments.report_fragments.DefaultAttendanceReportFragment
import com.example.attendance_app_2.sharedPrefs.SharedPrefs

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle
    val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val role = SharedPrefs.getRole(this)

        if (role == "teacher") {
            handleTeacherUI()
        } else if (role == "head") {
            handleHODUI()
        } else if (role == "admin") {
            handleAdminUI()
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
        val markAttendanceFragment = MarkAttendanceFragment()
        val seeAssignmentsFragment = SeeAssignmentsFragment()
        val updateAttendanceFragment = UpdateAttendanceFragment()
        val semesterDatesFragment = SemesterDatesFragment()


        //attendance report fragments
        val defaultAttendanceReportFragment = DefaultAttendanceReportFragment()
        val customAttendanceReportFragment = CustomAttendanceReportFragment()


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
                R.id.miDefaultAttendance -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, defaultAttendanceReportFragment).commit()
                    }
                }
                R.id.miCustomAttendance -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, customAttendanceReportFragment).commit()
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
                }R.id.miSemesterDates -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.container.id, semesterDatesFragment).commit()
                    }
                }
                R.id.miLogout -> {
                    //logout logic
                    SharedPrefs.clearUserDetails(this);
                    goToLoginScreen()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    fun goToLoginScreen(){
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    fun handleTeacherUI(){
        binding.navView.menu.findItem(R.id.miAssignClass).isVisible = false
        binding.navView.menu.findItem(R.id.miSeeAssignments).isVisible = false
        binding.navView.menu.findItem(R.id.miSemesterDates).isVisible = false
        binding.navView.menu.findItem(R.id.miCustomAttendance).isVisible = false
    }

    fun handleHODUI(){
        binding.navView.menu.findItem(R.id.miAssignClass).isVisible = false
        binding.navView.menu.findItem(R.id.miSemesterDates).isVisible = false
    }

    fun handleAdminUI(){
        binding.navView.menu.findItem(R.id.miUpdateAttendance).isVisible = false
        binding.navView.menu.findItem(R.id.miMarkAttendance).isVisible = false
        binding.navView.menu.findItem(R.id.miDefaultAttendance).isVisible = false
        binding.navView.menu.findItem(R.id.miCustomAttendance).isVisible = false
    }
}
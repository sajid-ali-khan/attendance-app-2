package com.example.attendance_app_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendance_app_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //check the sharePrefs
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val empId = sharedPreferences.getString("empId", null)

        if (empId != null){
            //intent to dashboard activity

        }

        val btnLogin = binding.btnLogin
        val etEmpId = binding.etEmpId
        val etPassword = binding.etPassword


    }
}
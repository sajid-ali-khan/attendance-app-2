package com.example.attendance_app_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.attendance_app_2.auth.AuthHelper
import com.example.attendance_app_2.auth.AuthResult
import com.example.attendance_app_2.databinding.ActivityMainBinding
import com.example.attendance_app_2.sharedPrefs.SharedPrefs
import kotlinx.coroutines.launch

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


        if (SharedPrefs.getEmpId(this) != null){
            //intent to dashboard activity
            goToDashboardActivity()
        }

        val btnLogin = binding.btnLogin
        val etEmpId = binding.etEmpId
        val etPassword = binding.etPassword


        btnLogin.setOnClickListener{
            val empId = etEmpId.text.toString()
            val password = etPassword.text.toString()

            //authenticate user
            lifecycleScope.launch{
                val authResult = AuthHelper.authenticate(this@MainActivity, empId, password)

                when (authResult){
                    AuthResult.SUCCESS -> {
                        showToast("Login Successful")
                        goToDashboardActivity()
                    }
                    AuthResult.USER_NOT_FOUND -> showToast("User Not Found")
                    AuthResult.INVALID_PASSWORD -> showToast("Invalid Password")
                    AuthResult.UNKNOWN_ERROR -> showToast("Unknown Error")
                }
            }

        }

    }

    private fun goToDashboardActivity() {
        Intent(this, DashboardActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    fun showToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

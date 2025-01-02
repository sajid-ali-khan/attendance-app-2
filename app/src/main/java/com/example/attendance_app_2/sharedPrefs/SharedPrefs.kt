package com.example.attendance_app_2.sharedPrefs

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {

    private const val PREF_NAME = "attendance_app_prefs"
    private const val KEY_EMP_ID = "empid"
    private const val KEY_NAME = "name"
    private const val KEY_ROLE = "role"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserDetails(context: Context, empId: String, name: String, role: String) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(KEY_EMP_ID, empId)
            putString(KEY_NAME, name)
            putString(KEY_ROLE, role)
            apply()
        }
    }

    fun getEmpId(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_EMP_ID, null)
    }

    fun getName(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_NAME, null)
    }

    fun getRole(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_ROLE, null)
    }

    fun clearUserDetails(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(KEY_EMP_ID)
            remove(KEY_NAME)
            remove(KEY_ROLE)
            apply()
        }
    }
}

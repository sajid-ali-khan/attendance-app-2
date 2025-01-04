package com.example.attendance_app_2.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomSpinnerAdapter(
    context: Context,
    private val hint: String,
    private var items: MutableList<String> // Use MutableList here
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {

    private val itemList = ArrayList<String>().apply { add(hint) } // Mutable ArrayList

    init {
        itemList.addAll(items) // Add the initial items
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getCount(): Int {
        return itemList.size // Exclude the hint from dropdown
    }

    override fun getItem(position: Int): String? {
        return itemList[position] // Skip hint
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = itemList[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = itemList[position]
        return view
    }

    // Method to update the list dynamically
    fun updateItems(newItems: MutableList<String>) {
        itemList.clear() // Clear the existing list
        itemList.add(hint) // Add the hint back as the first item
        itemList.addAll(newItems) // Add new items
        notifyDataSetChanged() // Refresh spinner view
    }
}



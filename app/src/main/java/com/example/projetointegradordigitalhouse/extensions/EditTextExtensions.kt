package com.example.projetointegradordigitalhouse

import android.app.DatePickerDialog
import android.text.TextWatcher
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

fun EditText.validateEmailFormat():Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
}

fun EditText.calendar() {
    this.isFocusableInTouchMode = false
    this.isClickable = true
    this.isFocusable = false

    fun updateLabel(myCalendar: Calendar, dateEditText: EditText) {
        val myFormat : String = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateEditText.setText(sdf.format(myCalendar.time))
    }

    val myCalendar = Calendar.getInstance()

    val datePickerOnDataSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel(myCalendar,this)
    }

    this.setOnClickListener {
        DatePickerDialog(context, datePickerOnDataSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(
            Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}

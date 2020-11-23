package com.example.projetointegradordigitalhouse

import android.text.TextWatcher
import android.widget.EditText

fun EditText.validateEmailFormat():Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
}


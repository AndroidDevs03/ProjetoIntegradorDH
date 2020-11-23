package com.example.projetointegradordigitalhouse.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.projetointegradordigitalhouse.util.MaskWatcher
import com.example.projetointegradordigitalhouse.databinding.ActivityRegisterBinding
import com.example.projetointegradordigitalhouse.validateEmailFormat
import java.util.*


class RegisterActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEdit()
    }

    override fun onStop() {
        clearEditText()
        super.onStop()
    }

    private fun validatePassword() : Boolean {
        return binding.etPasswordSU.text.toString().equals(binding.etConfirmPassword.text.toString())
    }

    private fun clearEditText () {
        binding.etBithDate.text?.clear()
        binding.etFullName.text?.clear()
        binding.etEmailSU.text?.clear()
        binding.etPasswordSU.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }

    private fun validateBirthday():Boolean{
        val date = binding.etBithDate.text.toString().split("/")
        val calendario = Calendar.getInstance()
        val atualYear = calendario.get(Calendar.YEAR)

        return if (date[1].toInt() == 1 ||date[1].toInt() == 3 ||date[1].toInt() == 5 ||date[1].toInt() == 7 ||date[1].toInt() == 8
            ||date[1].toInt() == 10 ||date[1].toInt() == 12 && date[0].toInt() < 32 && date[2].toInt() < atualYear){
            true
        }else {
            if (date[1].toInt() == 4 || date[1].toInt() == 6 || date[1].toInt() == 9 || date[1].toInt() == 11 && date[0].toInt() < 31 && date[2].toInt() < atualYear) {
                true
            } else {
                date[1].toInt() == 2 && date[0].toInt() < 29 && date[2].toInt() < atualYear
            }
        }
    }

    private fun initEdit (){
        binding.etBithDate.addTextChangedListener(MaskWatcher(binding.etBithDate, "##/##/####"))
        binding.etPasswordSU.addTextChangedListener(MaskWatcher(binding.etPasswordSU, "########"))
        binding.etConfirmPassword.addTextChangedListener(MaskWatcher(binding.etConfirmPassword,"########"))


        binding.btRegister.setOnClickListener {
            if (binding.etFullName.text.isNullOrEmpty() || binding.etBithDate.text.isNullOrEmpty() || binding.etEmailSU.text.isNullOrEmpty() || binding.etPasswordSU.text.isNullOrEmpty() || binding.etConfirmPassword.text.isNullOrEmpty()){
                Toast.makeText(this, "Obrigatótio o preenchimento de todos os campos", Toast.LENGTH_SHORT).show()
            }else{
                if(binding.etPasswordSU.length() != 8){
                    Toast.makeText(this, "Password deve ter 8 caracteres", Toast.LENGTH_SHORT).show()
                }else{
                    if (!validatePassword()){
                        Toast.makeText(this, "Password e Confirm Password devem ser iguais", Toast.LENGTH_SHORT).show()
                    }else{
                        if (binding.etFullName.length() > 0 && validateBirthday() && binding.etEmailSU.validateEmailFormat() && validatePassword()){
                            startActivity(Intent(this, RegisterSplash::class.java))
                            finish()
                        }else{
                            Toast.makeText(this, "Obrigatótio o preenchimento correto de todos os campos", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
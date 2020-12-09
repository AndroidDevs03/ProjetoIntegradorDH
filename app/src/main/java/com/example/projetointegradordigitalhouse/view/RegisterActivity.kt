package com.example.projetointegradordigitalhouse.view



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.projetointegradordigitalhouse.calendar
import com.example.projetointegradordigitalhouse.util.MaskWatcher
import com.example.projetointegradordigitalhouse.databinding.ActivityRegisterBinding
import com.example.projetointegradordigitalhouse.validateEmailFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {
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
        return binding.etPasswordSU.text.toString()==(binding.etConfirmPassword.text.toString())
    }

    private fun clearEditText () {
        binding.apply { binding.etBithDate.text?.clear()
            etFullName.text?.clear()
            etEmailSU.text?.clear()
            etPasswordSU.text?.clear()
            etConfirmPassword.text?.clear() }
    }

    private fun initEdit (){
        binding.apply {
            etPasswordSU.addTextChangedListener(MaskWatcher(etPasswordSU, "########"))
            etConfirmPassword.addTextChangedListener(MaskWatcher(etConfirmPassword,"########"))
            etBithDate.calendar()
        }

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
                        if (binding.etFullName.length() > 0 && binding.etBithDate.length()>0 && binding.etEmailSU.validateEmailFormat() && validatePassword()){
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
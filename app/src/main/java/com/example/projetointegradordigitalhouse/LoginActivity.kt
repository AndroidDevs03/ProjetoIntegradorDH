package com.example.projetointegradordigitalhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.digitalhousefoods_desafio2.utils.MaskWatcher
import com.example.projetointegradordigitalhouse.databinding.ActivityLoginBinding

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : AppCompatActivity(){
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initItems()
    }

    override fun onStop() {
        clearItems()
        super.onStop()
    }


    private fun initItems (){
        binding.etPasswordSI.addTextChangedListener(MaskWatcher(binding.etPasswordSI, "########"))

        binding.btSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btFacebook.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.btGoogle.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.btSignIn.setOnClickListener {
            if (binding.etPasswordSI.length() == 8 && binding.etEmailSI.validateEmailFormat()){
                startActivity(Intent(this, HomeActivity::class.java))
            }else {
                if (binding.etPasswordSI.length() != 8 && binding.etEmailSI.validateEmailFormat()){
                    Toast.makeText(this, "Password inválido", Toast.LENGTH_LONG).show()
                }else{
                    if (binding.etPasswordSI.length() == 6 && !(binding.etEmailSI.validateEmailFormat())){
                        Toast.makeText(this, "Email inválido", Toast.LENGTH_LONG).show()
                    }else {
                        if (binding.etPasswordSI.text.isNullOrEmpty() && binding.etEmailSI.text.isNullOrEmpty()){
                            Toast.makeText(this, "Preenchimento dos campos obrigatórios", Toast.LENGTH_LONG).show()
                        }else {
                            Toast.makeText(this, "Preenchimento e-mail e password inválidos", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun clearItems(){
        binding.etPasswordSI.text?.clear()
        binding.etEmailSI.text?.clear()
    }
}
package com.example.projetointegradordigitalhouse.view



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.calendar
import com.example.projetointegradordigitalhouse.util.MaskWatcher
import com.example.projetointegradordigitalhouse.databinding.ActivityRegisterBinding
import com.example.projetointegradordigitalhouse.model.Avatar
import com.example.projetointegradordigitalhouse.validateEmailFormat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.String as String1


@Suppress("CAST_NEVER_SUCCEEDS")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val firebaseFirestore by lazy {
        Firebase.firestore
    }

    private val auth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initEdit()

        auth.currentUser?.let{
            firebaseFirestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { snapshot ->
                    val userData = snapshot.data
                    val name = userData?.get("name")
                    val email = userData?.get("email")
                    val nameedit = name.toString()
                    val emailedit = email.toString()
                    binding.etFullName.setText(nameedit)
                    binding.etEmailSU.setText(emailedit)
                    binding.etEmailSU.isEnabled = false
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }?: run {

        }

        binding.btRegister.setOnClickListener {
            auth.currentUser?.let {
                firebaseFirestore.collection("users").document(it.uid).update("name",binding.etFullName.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dados editados com sucesso", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Não salvou", Toast.LENGTH_LONG).show()
                    }
            }?: run{

            }

        }
    }

    override fun onStop() {
        clearEditText()
        super.onStop()
    }

//    private fun validatePassword() : Boolean {
//        return binding.etPasswordSU.text.toString()==(binding.etConfirmPassword.text.toString())
//    }

    private fun clearEditText () {
        binding.apply {
            etFullName.text?.clear()
            etEmailSU.text?.clear()
        }
    }



//    private fun initEdit (){
//        binding.apply {
//            etPasswordSU.addTextChangedListener(MaskWatcher(etPasswordSU, "########"))
//            etConfirmPassword.addTextChangedListener(MaskWatcher(etConfirmPassword,"########"))
//            etBithDate.calendar()
//        }

//        binding.btRegister.setOnClickListener {
//            if (binding.etFullName.text.isNullOrEmpty() || binding.etBithDate.text.isNullOrEmpty() || binding.etEmailSU.text.isNullOrEmpty() || binding.etPasswordSU.text.isNullOrEmpty() || binding.etConfirmPassword.text.isNullOrEmpty()){
//                Toast.makeText(this, "Obrigatótio o preenchimento de todos os campos", Toast.LENGTH_SHORT).show()
//            }else{
//                if(binding.etPasswordSU.length() != 8){
//                    Toast.makeText(this, "Password deve ter 8 caracteres", Toast.LENGTH_SHORT).show()
//                }else{
//                    if (!validatePassword()){
//                        Toast.makeText(this, "Password e Confirm Password devem ser iguais", Toast.LENGTH_SHORT).show()
//                    }else{
//                        if (binding.etFullName.length() > 0 && binding.etBithDate.length()>0 && binding.etEmailSU.validateEmailFormat() && validatePassword()){
//                            startActivity(Intent(this, RegisterSplash::class.java))
//                            finish()
//                        }else{
//                            Toast.makeText(this, "Obrigatótio o preenchimento correto de todos os campos", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }
//        }
//    }

}
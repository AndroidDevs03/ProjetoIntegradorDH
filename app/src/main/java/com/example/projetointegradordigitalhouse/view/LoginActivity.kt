package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding

    private val firebaseAuth by lazy{
        Firebase.auth
    }

    private var customLayout = AuthMethodPickerLayout.Builder(R.layout.activity_login)
        .setGoogleButtonId(R.id.btGoogle)
        .setEmailButtonId(R.id.btSignIn)
        .setFacebookButtonId(R.id.btFacebook)
        .setAnonymousButtonId(R.id.btSignAnonimous)
        .build()


    private val providers by lazy {
        arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        initSignUp()
//        sutupObservables()
    }

    override fun onResume(){
        super.onResume()
        firebaseAuth.currentUser?.let{
            //TODO Firestore
        }
    }

//    private fun setupObservables() {
//        binding.btSignUp.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//
//    }

    private fun initSignUp() {

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.FirebaseTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN){
            val response = fromResultIntent(data)
//            Toast.makeText(this, "requestCode: ${requestCode}", Toast.LENGTH_LONG).show()
            if (resultCode == Activity.RESULT_OK){
                // Successfully signed in
                startScreen()
                registerUser(firebaseAuth.currentUser)
            } else{
                startActivity(Intent(this, SplashActivity::class.java))
                Toast.makeText(this, "Falha de autenticação: ${response?.getError()?.getErrorCode()}", Toast.LENGTH_LONG).show()
//                Toast.makeText(this, "Erro: ${response?.getError()?.getErrorCode()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun registerUser(user: FirebaseUser?) {
        user?.let{
            //todo
        }
    }

    companion object {
        private const val RC_SIGN_IN = 999
    }

}
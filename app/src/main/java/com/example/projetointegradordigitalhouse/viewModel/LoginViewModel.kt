package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.model.Avatar
import com.example.projetointegradordigitalhouse.model.User
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

@Suppress("CAST_NEVER_SUCCEEDS")
class LoginViewModel(
    context: Context
) : ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

//    var homeLoginUser: MutableLiveData<List<User>> = MutableLiveData()


    fun registerUser(firebaseUser: FirebaseUser?) {
        Log.i("HomeViewModel", "Registro de usuário")
        firebaseUser?.let {
            viewModelScope.launch {
                        val tempUser = User(
                            it.uid,
                            0,
                            it.displayName.toString(),
                            it.email.toString(),
                            null,
                            "Data"
                        )
                        repository.setUser(tempUser)
//                    homeLoginUser.postValue(tempList)
                    }
            }
//                val tempUser = User(
//                    firebaseUser.uid,
//                    ,
//                    it.displayName.toString(),
//                    it.email.toString(),
//                    null,
//                    "Data"
//                )
//                viewModelScope.launch {
//                    repository.setUser(tempUser)
////                    homeLoginUser.postValue(tempList)
//                }
        }
    }

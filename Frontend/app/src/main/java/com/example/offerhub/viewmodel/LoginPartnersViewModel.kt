package com.example.offerhub.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.util.Constants
import com.example.offerhub.util.RegisterFieldsState
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.util.validateEmail
import com.example.offerhub.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginPartnersViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String){
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        val shouldLogin =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        if (shouldLogin) {
            viewModelScope.launch { _login.emit(Resource.Loading()) }
            firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                        sharedPreferences.edit().putString(Constants.USER_STATUS,"partner").apply()
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
        }else{
            val loginFieldsState = RegisterFieldsState(
                validateEmail(email), validatePassword(password)
            )

        }
    }






    fun resetPassword(email: String){
            viewModelScope.launch {
                _resetPassword.emit(Resource.Loading())

            }

                firebaseAuth
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _resetPassword.emit(Resource.Success(email))
                        }
                    }.addOnFailureListener{
                        viewModelScope.launch {
                            _resetPassword.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }

}
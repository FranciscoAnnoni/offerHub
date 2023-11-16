package com.example.offerhub.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.contexts.ApplicationContextProvider
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
class PromoNotiDetailViewModel @Inject constructor(
): ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()
    private val sharedPreferences: SharedPreferences =
        ApplicationContextProvider.getApplicationContext().getSharedPreferences("user_view_model_cache", Context.MODE_PRIVATE)


    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun startActivity(){
        viewModelScope.launch {
            sharedPreferences.edit().putString(Constants.USER_STATUS,"user").apply()
        }

    }


}
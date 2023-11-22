package com.example.offerhub.viewmodel

import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.Funciones
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentIntroductionBinding
import com.example.offerhub.util.Constants.INTRODUCTION_KEY
import com.example.offerhub.util.Constants.USER_STATUS
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IntroductionViewModel @Inject constructor (
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object{
        const val SHOPPING_ACTIVITY = 23
        const val ACCOUNT_OPTIONS_FRAGMENT = 1000053
        const val SHOPPING_ACTIVITY_PARTNERS = 25
        const val SHOPPING_ACTIVITY_ADMIN = 24
    }

    init {
        val isButtonChecked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val isPartner = sharedPreferences.getString(USER_STATUS, "user")
        val user = firebaseAuth.currentUser

        if (user != null) {
            if (isPartner == "partner") {
                viewModelScope.launch {
                    // El ID del usuario está en la lista de partners
                    _navigate.emit(SHOPPING_ACTIVITY_PARTNERS)
                }
            }
            if (isPartner == "user"){
                viewModelScope.launch {
                    // El ID del usuario no está en la lista de partners
                    _navigate.emit(SHOPPING_ACTIVITY)
                }
            }
            if (isPartner == "parterRegister"){
                viewModelScope.launch {
                    // El ID del usuario no está en la lista de partners
                    _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
                }
            }
            if (isPartner == "userAdmin"){
                viewModelScope.launch {
                    // El ID del usuario no está en la lista de partners
                    _navigate.emit(SHOPPING_ACTIVITY_ADMIN)
                }
            }


        } else if (isButtonChecked) {

            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        } else {

            Unit
        }
    }


    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }



}



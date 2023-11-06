package com.example.offerhub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.Funciones
import com.example.offerhub.Usuario
import com.example.offerhub.data.UserPartner
import com.example.offerhub.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PartnersProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth

):ViewModel() {

    // val logoutSuccessLiveData = MutableLiveData<Boolean>() par el log out exitoso
    private val _userPartner = MutableStateFlow<Resource<UserPartner>>(Resource.Unspecified())
    val userPartner = _userPartner.asStateFlow()

    init {
        getUser()
    }

    //OBTENEMOS EL USUAREIO EN CUESTION
    fun getUser() {


    }

    fun logout(){
        UserViewModelCache().vaciarCache()
        auth.signOut()

    }

}
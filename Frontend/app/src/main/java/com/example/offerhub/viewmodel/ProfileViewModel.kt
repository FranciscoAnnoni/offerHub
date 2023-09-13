package com.example.offerhub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.Funciones
import com.example.offerhub.Usuario
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
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth

):ViewModel() {

    private val _user = MutableStateFlow<Resource<Usuario>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    //OBTENEMOS EL USUAREIO EN CUESTION
    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val instancia = Funciones()
                val usuario = instancia.traerUsuarioActual()

                viewModelScope.launch {
                    if(usuario != null){
                        _user.emit(Resource.Success(usuario))
                    }else {
                        viewModelScope.launch {
                            _user.emit(Resource.Error("error en visualizar usuario"))
                        }
                    }

                }

            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }

    }

    fun logout(){
        auth.signOut()
    }

}
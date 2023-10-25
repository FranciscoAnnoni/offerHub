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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class NotificarErrorViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    private val _notificacionExitosa = MutableSharedFlow<Resource<String>>()
    val notificacionExitosa = _notificacionExitosa.asSharedFlow()

    fun enviarNotificacionDeError(notificacionDeError: String){

            if (notificacionDeError != "") {
                viewModelScope.launch { _notificacionExitosa.emit(Resource.Loading()) }

                val database: FirebaseDatabase =
                    FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
                val referencia: DatabaseReference = database.reference.child("/Reportes")

                referencia.push().setValue(notificacionDeError)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _notificacionExitosa.emit(Resource.Success("exitoso"))
                        }
                    }.addOnFailureListener{
                        viewModelScope.launch {
                            _notificacionExitosa.emit(Resource.Error("error"))
                        }
                    }
            }else{

                /*
                val loginFieldsState = RegisterFieldsState(
                    validateEmail(email), validatePassword(password)
                )

                 */

            }

    }

}
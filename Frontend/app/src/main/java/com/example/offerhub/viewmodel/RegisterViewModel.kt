package com.example.offerhub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.offerhub.EscribirBD
import com.example.offerhub.Usuario
import com.example.offerhub.data.User
import com.example.offerhub.util.Constants.USER_COLLECTION
import com.example.offerhub.util.RegisterFieldsState
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.util.validateEmail
import com.example.offerhub.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

// lo que hace aparenemente es injenctar dependencias en loas viweModels

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    // private val db: FirebaseDatabase

): ViewModel(){

    private val _register = MutableStateFlow<Resource<Usuario>>(Resource.Unspecified())
          val register:Flow<Resource<Usuario>> = _register

    private val _validation = Channel<RegisterFieldsState>()
        val validation = _validation.receiveAsFlow()


    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    fun createAccountWithEmailAndPassword(user: User, password:String) {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success


        if (shouldRegister) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid,user)
                        //_register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }

        }else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }

        }

    }

    private fun saveUserInfo(userUid: String, user: User){
       val usuario = Usuario(
           userUid,
           user.nameAndLastName,
           user.email,
           listOf(),
           listOf(),
           listOf(),
           listOf(),
           listOf()
       )

        val database: FirebaseDatabase =
                FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.getReference("/Usuario")

        referencia.child(userUid).child("correo").setValue(user.email)
        referencia.child(userUid).child("nombre").setValue(user.nameAndLastName)
           .addOnSuccessListener {
                _registrationSuccess.value = true // Registro exitoso
                _register.value = Resource.Success(usuario)
            }.addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }
    }



}
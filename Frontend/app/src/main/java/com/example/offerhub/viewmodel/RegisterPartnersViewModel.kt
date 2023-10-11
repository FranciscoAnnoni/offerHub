package com.example.offerhub.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.offerhub.EscribirBD
import com.example.offerhub.Usuario
import com.example.offerhub.data.User
import com.example.offerhub.data.UserPartner
import com.example.offerhub.util.Constants.USER_COLLECTION
import com.example.offerhub.util.RegisterFieldsState
import com.example.offerhub.util.RegisterFieldsStatePartner
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.util.validateCuil
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
class RegisterPartnersViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    // private val db: FirebaseDatabase

): ViewModel(){

    private val _register = MutableStateFlow<Resource<UserPartner>>(Resource.Unspecified())
          val register:Flow<Resource<UserPartner>> = _register

    private val _validation = Channel<RegisterFieldsStatePartner>()
        val validation = _validation.receiveAsFlow()


    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess


    // ESTA FUNCUION HAY QUE CAMBIARLA PARA QUE TENGA AL USUARIO CORRECTO
    fun createAccountWithEmailAndPassword(usuario: UserPartner, password:String) {
        Log.d("EMAIL vm", "${ usuario.email }")
        val emailValidation = validateEmail(usuario.email)
        val cuilValidation = validateCuil(usuario.cuil)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success &&cuilValidation is RegisterValidation.Success


        if (shouldRegister) {

            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(usuario.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        Log.d("ID", "${ it.uid }")
                        saveUserInfo(it.uid,usuario)
                        //_register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }

        }else {
            val registerFieldsState = RegisterFieldsStatePartner(
                validateEmail(usuario.email), validatePassword(password), validateCuil(usuario.cuil)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }

        }

    }

    private fun saveUserInfo(userUid: String, user:UserPartner ){
       val usuario = UserPartner(
           userUid,
           user.nombreDeEmpresa,
           user.cuil,
           user.email,
           listOf()
       )
        Log.d("cantifnlas", "ENTRE")
        val database: FirebaseDatabase =
                FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.getReference("/UsuarioPartner")

        referencia.child(userUid).child("nombre").setValue(user.nombreDeEmpresa)
        referencia.child(userUid).child("cuil").setValue(user.cuil)
        referencia.child(userUid).child("correo").setValue(user.email)

           .addOnSuccessListener {
                _registrationSuccess.value = true // Registro exitoso
                _register.value = Resource.Success(usuario)
            }.addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }
    }

    fun logout(){
        firebaseAuth.signOut()
    }


}
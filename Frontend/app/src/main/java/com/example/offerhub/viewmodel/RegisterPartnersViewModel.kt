package com.example.offerhub.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.offerhub.Funciones
import com.example.offerhub.data.UserPartner
import com.example.offerhub.util.Constants
import com.example.offerhub.util.RegisterFieldsStatePartner
import com.example.offerhub.util.RegisterFieldsStatePartnerCuil
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.util.validateCuil
import com.example.offerhub.util.validateEmail
import com.example.offerhub.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    private val sharedPreferences: SharedPreferences,
): ViewModel(){

    val usuario = firebaseAuth.currentUser;
    val userUid = usuario?.uid
    val instancia = Funciones()

    var onRegistrationFailure: (() -> Unit)? = null

    private val _register = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
          val register:Flow<Resource<Boolean>> = _register


    private val _registerUser = MutableStateFlow<Resource<UserPartner>>(Resource.Unspecified())
    val registerUser:Flow<Resource<UserPartner>> = _registerUser

    private val _validation = Channel<RegisterFieldsStatePartner>()
        val validation = _validation.receiveAsFlow()

    private val _validationUser = Channel<RegisterFieldsStatePartnerCuil>()
    val validationUser = _validationUser.receiveAsFlow()

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess


    // ESTA FUNCUION HAY QUE CAMBIARLA PARA QUE TENGA AL USUARIO CORRECTO
    fun createAccountWithEmailAndPassword(email:String, password:String) {

        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        if (shouldRegister) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        sharedPreferences.edit().putString(Constants.PARTNER_USER,"parterRegister").apply()
                        _register.value = Resource.Success(true)
                    }
                }.addOnFailureListener {
                    onRegistrationFailure?.invoke()
                    _register.value = Resource.Error(it.message.toString())
                }

        }else {
            val registerFieldsState = RegisterFieldsStatePartner(
                validateEmail(email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    fun createAccountUserPartner(user:UserPartner) {
        val cutValidation = validateCuil(user.cuil)
        val shouldRegister = cutValidation is RegisterValidation.Success

        if (shouldRegister) {
            runBlocking {
                _registerUser.emit(Resource.Loading())
            }


            if (userUid != null) {
                saveUserInfo(userUid,user)
            }


        }else {
            val registerFieldsState = RegisterFieldsStatePartnerCuil(
                 validateCuil(user.cuil)
            )
            runBlocking {
                _validationUser.send(registerFieldsState)
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

        val database: FirebaseDatabase =
                FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.getReference("/UsuarioPartner")

        referencia.child(userUid).child("nombre").setValue(user.nombreDeEmpresa)
        referencia.child(userUid).child("cuil").setValue(user.cuil)
        referencia.child(userUid).child("correo").setValue(user.email)

           .addOnSuccessListener {
                _registrationSuccess.value = true // Registro exitoso
                _registerUser.value = Resource.Success(usuario)
            }.addOnFailureListener{
                _registerUser.value = Resource.Error(it.message.toString())
            }
    }


    fun logout(){
        firebaseAuth.signOut()
    }

    fun deleteUser() {
        // Current signed-in user to delete
        firebaseAuth.signOut()
        usuario?.delete()
        if (userUid != null) {
            instancia.elimiarUsuario(userUid)
        }


    }

}
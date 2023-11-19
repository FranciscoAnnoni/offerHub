package com.example.offerhub.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.Funciones
import com.example.offerhub.Usuario
import com.example.offerhub.data.UserPartner
import com.example.offerhub.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartnersUserAccountViewModel @Inject constructor(
    app: Application,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) : AndroidViewModel(app) {

    private val _userPartner = MutableStateFlow<Resource<UserPartner>>(Resource.Unspecified())
    val userPartner = _userPartner.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<UserPartner?>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
    val referencia: DatabaseReference = database.getReference("/UsuarioPartner")

    init {
        getUser()
    }

    val usuario = auth.currentUser;
    val userUid = usuario?.uid
    val instancia = Funciones()

    //OBTENEMOS EL USUAREIO EN CUESTION
    fun getUser() {
        viewModelScope.launch {
            _userPartner.emit(Resource.Loading())
        }

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val usuarioPartner = instancia.traerUsuarioPartner()

                viewModelScope.launch {
                    if(usuarioPartner != null){
                        _userPartner.emit(Resource.Success(usuarioPartner))
                    }else {
                        viewModelScope.launch {
                            _userPartner.emit(Resource.Error("error en visualizar usuario"))
                        }
                    }

                }

            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }

    }


    fun updateUser(nombreyApellido: String){
        val areInputsValid = nombreyApellido.trim().isNotEmpty()

        if (!areInputsValid){
            viewModelScope.launch {
                _userPartner.emit(Resource.Error("No pueden quedar campos vacios"))
            }
            return
        }

        viewModelScope.launch{
            _updateInfo.emit(Resource.Loading())
        }

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val usuarioPartner = instancia.traerUsuarioPartner()

                userUid?.let { it1 -> instancia.editarPerfilPartner(it1,"nombre",nombreyApellido) }

                viewModelScope.launch {
                    if(usuario != null){
                        delay(1000)
                        _updateInfo.emit(Resource.Success(usuarioPartner))
                    }else {
                        viewModelScope.launch {
                            _updateInfo.emit(Resource.Error("error de guardado"))
                        }
                    }

                }

            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }


    }


    fun deleteUser() {
        // Current signed-in user to delete
        auth.signOut()
        usuario?.delete()
        if (userUid != null) {
            //instancia.elimiarUsuario(userUid)
        }


    }

     /*
    //GUARDAR EL USUARIO
    private fun saveUserInformation(nombreyApellido: String) {
        val userUid = auth.currentUser?.uid

        if (userUid != null) {
            val userReference = referencia.child(userUid)

            // Crea un mapa para actualizar solo el nombre y apellido
            val updates = HashMap<String, Any>()
            updates["nameAndLastName"] = user.nombre

            userReference.updateChildren(updates)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _updateInfo.emit(Resource.Success(user))
                    }
                }
                .addOnFailureListener { error ->
                    viewModelScope.launch {
                        _updateInfo.emit(Resource.Error(error.message.toString()))
                    }
                }
        } else {
            // Handle the case where the user is not authenticated.
        }
    }
 */


}

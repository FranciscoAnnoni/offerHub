package com.example.offerhub.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offerhub.Funciones
import com.example.offerhub.Usuario
import com.example.offerhub.data.User
import com.example.offerhub.util.RegisterValidation
import com.example.offerhub.util.Resource
import com.example.offerhub.util.validateEmail
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    app: Application,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<Usuario>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<Usuario>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
    val referencia: DatabaseReference = database.getReference("/Usuario")


    init {
        getUser()
    }

    val usuario = auth.currentUser;
    val userUid = usuario?.uid
    val instancia = Funciones()


    //OBTENEMOS EL USUAREIO EN CUESTION
    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {

               val usuario = instancia.traerUsuarioActual()

                viewModelScope.launch {
                    if(usuario != null){
                        _user.emit(Resource.Success(usuario))
                    }else {
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
                _user.emit(Resource.Error("No pueden quedar campos vacios"))
            }
            return
        }

        viewModelScope.launch{
            _updateInfo.emit(Resource.Loading())
        }

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {

                userUid?.let { it1 -> instancia.editarPerfil(it1,"nombre",nombreyApellido) }

                val usuario = instancia.traerUsuarioActual()

                viewModelScope.launch {
                    if(usuario != null){
                        delay(1000)
                        _updateInfo.emit(Resource.Success(usuario))
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
            instancia.elimiarUsuario(userUid)
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

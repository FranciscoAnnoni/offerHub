package com.example.offerhub.viewmodel

import UserViewModel
import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.contexts.ApplicationContextProvider
import com.example.offerhub.funciones.FuncionesPartners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object UserViewModelSingleton {
    private var userViewModel: UserViewModel? = null

    fun initialize(userViewModel: UserViewModel) {
        this.userViewModel = userViewModel


    }

    fun getSingleUserViewModel(): UserViewModel? {
        return userViewModel
    }


    fun getUserViewModel(create:Boolean=true): UserViewModel {

        val userViewModelCache = UserViewModelCache()

        Log.d("UVM","Buscando")
        val userViewModelCacheado = userViewModelCache.cargarUserViewModel()
        if (userViewModelCacheado != null && userViewModelCacheado.usuario!=null) {
            Log.d("UVM","Usuario not null")
            initialize(userViewModelCacheado)
            } else if(create) {
                Log.d("Singleton","No encontro nada, recrea")
                val uvm=UserViewModel()
                var job=CoroutineScope(Dispatchers.Main).launch {
                uvm.usuario = Funciones().traerUsuarioActual()
                    val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
                    val currentUser = auth.currentUser
                    if(currentUser!=null) {
                        if(currentUser.email!="admin@offerhub.com"){
                            Log.d("Desde uvm","1")
                        uvm.listadoDePromosDisp = Funciones().obtenerPromociones(uvm.usuario!!)

                        uvm.tarjetasDisponibles = Funciones().obtenerTarjetasDisponibles()
                        } else {
                            uvm.listadoDePromosDisp = FuncionesPartners().obtenerPromosPendientes()
                        }
                    }
                userViewModelCache.guardarUserViewModel(uvm)
                }.invokeOnCompletion {

                    val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
                    val currentUser = auth.currentUser
                    var job=CoroutineScope(Dispatchers.Main).launch {
                        if (currentUser != null && currentUser.email != "admin@offerhub.com") {
                            uvm.favoritos = Funciones().obtenerPromocionesFavoritas(uvm.usuario!!,
                                uvm.listadoDePromosDisp as MutableList<Promocion>
                            )
                            uvm.reintegros = Funciones().obtenerPromocionesReintegro(uvm.usuario!!,uvm.listadoDePromosDisp as MutableList<Promocion>)
                        }
                    }
                }
                initialize(uvm)
            }

        return userViewModel?:UserViewModel()
    }
}

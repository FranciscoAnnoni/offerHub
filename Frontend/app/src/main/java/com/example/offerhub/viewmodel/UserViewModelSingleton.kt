package com.example.offerhub.viewmodel

import UserViewModel
import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.offerhub.Funciones
import com.example.offerhub.contexts.ApplicationContextProvider
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

            val userViewModelCacheado = userViewModelCache.cargarUserViewModel()
            if (userViewModelCacheado != null && userViewModelCacheado.usuario!=null) {
                initialize(userViewModelCacheado)
            } else if(create) {
                Log.d("Singleton","No encontro nada, recrea")
                val uvm=UserViewModel()
                CoroutineScope(Dispatchers.Main).launch {
                uvm.usuario = Funciones().traerUsuarioActual()
                uvm.listadoDePromosDisp = Funciones().obtenerPromociones(uvm.usuario!!)
                uvm.favoritos = Funciones().obtenerPromocionesFavoritas(uvm.usuario!!)
                uvm.reintegros = Funciones().obtenerPromocionesReintegro(uvm.usuario!!)
                userViewModelCache.guardarUserViewModel(uvm)
                }
                initialize(uvm)
            }

        return userViewModel?:UserViewModel()
    }
}

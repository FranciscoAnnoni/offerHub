package com.example.offerhub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.offerhub.Funciones
import com.example.offerhub.Tarjeta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TarjetasViewModel: ViewModel() {
    var tarjetasDisponibles: MutableList<Tarjeta> = mutableListOf()
}

object TarjetasViewModelSingleton {
    private var tarjetasViewModel: TarjetasViewModel? = null

    fun initialize(tarjetasViewModel: TarjetasViewModel) {
        this.tarjetasViewModel = tarjetasViewModel
    }


}

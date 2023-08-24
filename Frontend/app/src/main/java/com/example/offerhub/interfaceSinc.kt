package com.example.offerhub

import android.util.Log
import kotlinx.coroutines.CompletableDeferred

class InterfaceSinc{
    suspend inline fun <reified T> leerBdClaseSinc(tabla: String, campoFiltro: String, valorFiltro: String): MutableList<T> {
        val deferred = CompletableDeferred<MutableList<T>>()
        var instancia = LecturaBD()
        instancia.leerBdClase(tabla, campoFiltro, valorFiltro) { lista ->
            deferred.complete(lista)
        }

        return deferred.await()
    }

}
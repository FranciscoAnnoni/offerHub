package com.example.offerhub

import android.util.Log
import kotlinx.coroutines.CompletableDeferred

class InterfaceSinc{
    suspend inline fun <reified T> leerBdClaseSinc(tabla: String, campoFiltro: String, valorFiltro: String): MutableList<T> {
        val deferred = CompletableDeferred<MutableList<T>>()
        var instancia = LecturaBD()
        instancia.traerClasesXFiltro(tabla, campoFiltro, valorFiltro) { lista ->
            deferred.complete(lista)
        }
        return deferred.await()
    }

    suspend inline fun leerBdStringSinc(tabla: String, campoFiltro: String, valorFiltro: String, campoRetorno:String): MutableList<String> {
        val deferred = CompletableDeferred<MutableList<String>>()
        var instancia = LecturaBD()
        instancia.leerBdString(tabla, campoFiltro, valorFiltro,campoRetorno) { lista ->
            deferred.complete(lista)
        }
        return deferred.await()
    }


}

/*
Ejemplo llamado:
        var instancia = InterfaceSinc()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var lista: MutableList<String> = mutableListOf()
        setContentView(R.layout.activity_main)
        val job = coroutineScope.launch {
            try {
                lista =
                    instancia.leerBdStringSinc("Promocion", "categoria", "Gastronomía", "titulo")
                for (data in lista){
                    Log.d("Promocion", "titulo: $data")
                }
            }
            catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }
 */
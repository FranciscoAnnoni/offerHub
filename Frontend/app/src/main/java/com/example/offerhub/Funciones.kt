package com.example.offerhub

import android.util.Log
import kotlinx.coroutines.*


class Funciones {
    suspend fun listarPromociones(usuario: Usuario): List<Promocion> = coroutineScope {
        val listaPromos: MutableList<Promocion> = mutableListOf()
        val instancia = LecturaBD()
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        val deferredPromos = usuario.tarjetas?.map { tarjeta ->
            coroutineScope.async {
                val promos = tarjeta?.let { instancia.obtenerPromosPorTarjetas(it) }
                if (promos != null) {
                    listaPromos.addAll(promos)
                }
            }
        }

        deferredPromos?.awaitAll()

        listaPromos
    }
}

//ejemplos llamados
/*
var instancia = Funciones()
        setContentView(R.layout.activity_main)
        var tarjetas: List<String?> = listOf("-NbqSvUq5L_kTqrzlUo_")
        var usuario1 = Usuario("Pepe", "usuario1@example.com", tarjetas)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            try {
                var promociones = instancia.listarPromociones(usuario1)
                println("Promociones obtenidas:")
                for (promo in promociones){
                    promo.titulo?.let { Log.d("promo", it) }
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }
 */

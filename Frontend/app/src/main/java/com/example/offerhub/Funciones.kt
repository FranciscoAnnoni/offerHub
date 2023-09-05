package com.example.offerhub

import android.util.Log
import kotlinx.coroutines.*

class Funciones {

    val instancia = LecturaBD()
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    //Obtiene las promociones de comercios que aplican a cualq usuario, sin necesidad de tarjetas.
    suspend fun obtenerPromocionesComunes(): List<Promocion> = coroutineScope {
        val listaPromos: MutableList<Promocion> = mutableListOf()
        val promosDeferred = async {
            instancia.obtenerPromosPorTarjeta("No posee")
        }
        val promos = promosDeferred.await() // Esperar a que se completen las promociones
        if (promos != null) {
            listaPromos.addAll(promos)
        }
        listaPromos
    }

    suspend fun listarPromociones(usuario: Usuario): List<Promocion> = coroutineScope {
        val listaPromos: MutableList<Promocion> = mutableListOf()
        val deferredPromos = usuario.tarjetas?.map { tarjeta ->
            coroutineScope.async {
                val promos = tarjeta?.let { instancia.obtenerPromosPorTarjeta(it) }
                if (promos != null) {
                    listaPromos.addAll(promos)
                }
            }
        }

        deferredPromos?.awaitAll()

        listaPromos
    }


    suspend fun obtenerTarjeta(entidad: String, procesadora: String, segmento: String, tipo:String):Tarjeta? {
        var instancia = leerId()
        var interfaz = InterfaceSinc()
        var tarjetaFinal: Tarjeta? = null
        val resultado = instancia.obtenerIdSinc("Entidad", "nombre", entidad)
        if (resultado != null) {
            val listaEntidades: MutableList<Tarjeta> = interfaz.leerBdClaseSinc<Tarjeta>("Tarjeta","entidad",resultado)
            for (tarjeta in listaEntidades){
                if(tarjeta.procesadora==procesadora && tarjeta.segmento == segmento && tarjeta.tipoTarjeta == tipo){
                    tarjetaFinal = tarjeta
                }
            }
        }
        return tarjetaFinal
    }

    fun agregarPromocionAFavoritos(userId: String, elementoId: String) {
        EscribirBD().agregarElementoAListas(userId, elementoId, "Usuario", "favoritos")
    }

    fun elimiarPromocionDeFavoritos(userId: String, elementoId: String){
        EscribirBD().eliminarElementoDeListas(userId, elementoId, "Usuario", "favoritos")
    }

    fun agregarPromocionAReintegro(userId: String, elementoId: String) {
        EscribirBD().agregarElementoAListas(userId, elementoId, "Usuario", "promocionesReintegro")
    }

    fun elimiarPromocionDeReintegro(userId: String, elementoId: String){
        EscribirBD().eliminarElementoDeListas(userId, elementoId, "Usuario", "promocionesReintegro")
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


 Obtener tarjeta:
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val resultado = instancia.obtenerTarjeta("Banco Galicia", "Visa", "No posee","Débito")
                if (resultado != null) {
                    resultado.entidad?.let { Log.d("Resultado", it) }
                    resultado.id?.let { Log.d("Resultado", it) }
                }
                else{
                   Log.d("Resultado","NULO")
                }
            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }

  ObtenerPromocionesComunes:
          val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            try {
                var promociones = instancia.obtenerPromocionesComunes()
                println("Promociones obtenidas:")
                for (promo in promociones){
                    promo.titulo?.let { Log.d("promo", it) }
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }


AGREGAR FAVORITOS

        var instancia = Funciones()
        instancia.agregarPromocionAFavoritos("-Ndatw54kEPtRjXZ1dDw","-NcDHhG4OLbring2tKyp")

ELIMINAR FAVORITOS

        var instancia = Funciones()
        instancia.elimiarPromocionDeFavoritos("-Ndatw54kEPtRjXZ1dDw","-NcDHhG4OLbring2tKyp")

AGREGAR REINTEGRO

        var instancia = Funciones()
        instancia.agregarPromocionAReintegro("-Ndatw54kEPtRjXZ1dDw","-NcDHhG4OLbring2tKyp")

ELIMINAR REINTEGRO

        var instancia = Funciones()
        instancia.elimiarPromocionDeReintegro("-Ndatw54kEPtRjXZ1dDw","-NcDHhG4OLbring2tKyp")

 */

package com.example.offerhub

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.offerhub.data.UserPartner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LeerId {

    suspend fun obtenerIdSinc(tabla: String, campoFiltro: String, valorFiltro: String): String? {
        val deferred = CompletableDeferred<String?>()

        leerIdUnico(tabla, campoFiltro, valorFiltro) { id ->
            deferred.complete(id)
        }

        return deferred.await()
    }

    suspend fun obtenerIdsSinc(tabla: String, campoFiltro: String, valorFiltro: String): (List<String>) {
        val deferred = CompletableDeferred<String?>()
        val lista: MutableList<String> = mutableListOf()
        leerListaIds(tabla, campoFiltro, valorFiltro) { ids ->
            deferred.complete(ids.toString())
            lista.addAll(ids)
        }
        deferred.await()
        return lista
    }

    fun leerIdUnico(tabla: String,campoFiltro: String,valorFiltro: String,callback: (String?) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/$tabla")
        val lista: MutableList<String> = mutableListOf()
        promocionRef.orderByChild("$campoFiltro").equalTo(valorFiltro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val id = data.key
                        callback(id)
                        return


                    }

                }
                callback(null)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("DB - Error","Error en lectura de bd")
            }
        })
    }


    fun leerListaIds(tabla: String,campoFiltro: String,valorFiltro: String,callback:  (List<String>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/$tabla")
        val lista: MutableList<String> = mutableListOf()
        promocionRef.orderByChild("$campoFiltro").equalTo(valorFiltro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ids: MutableList<String> = mutableListOf()
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val idUnico = data.key
                        idUnico?.let {
                            ids.add(it)
                        }
                    }
                }
                callback(ids)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("DB - Error","Error en lectura de bd")
            }
        })
    }

    suspend fun obtenerEntidadPorId(id: String): Entidad? {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Entidad").child(id).get().await()

        if (dataSnapshot.exists()) {
            val key = dataSnapshot.key
            val nombre = dataSnapshot.child("nombre").getValue(String::class.java)
            val tipo = dataSnapshot.child("tipo").getValue(String::class.java)
            val entidad = Entidad(key, nombre, tipo)
            return entidad
        } else {
            return null
        }
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario? = withContext(Dispatchers.IO) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Usuario").child(id).get().await()

        if (dataSnapshot.exists()) {
            val correo = dataSnapshot.child("correo").getValue(String::class.java)
            val homeModoFull = dataSnapshot.child("homeModoFull").getValue(String::class.java)?:"0"
            val nombre = dataSnapshot.child("nombre").getValue(String::class.java) ?: ""
            val tarjetas = dataSnapshot.child("tarjetas").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})
            val favoritos = dataSnapshot.child("favoritos").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})
            val wishlistComercio = dataSnapshot.child("wishlistComercio").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})
            val wishlistRubro = dataSnapshot.child("wishlistRubro").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})
            val promocionesReintegro = dataSnapshot.child("promocionesReintegro").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})

            Usuario(id, nombre, correo, tarjetas, favoritos, wishlistComercio, wishlistRubro, promocionesReintegro,homeModoFull)
        } else {
            Log.d("DB - ID", "El usuario es NULO")
            null // El usuario no existe
        }
    }

    suspend fun obtenerUsuarioPartnerPorId(id: String): UserPartner? = withContext(Dispatchers.IO) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("UsuarioPartner").child(id).get().await()

        if (dataSnapshot.exists()) {
            val correo = dataSnapshot.child("correo").getValue(String::class.java)?:""
            val cuil = dataSnapshot.child("cuil").getValue(String::class.java)?:""
            val nombre = dataSnapshot.child("nombre").getValue(String::class.java) ?: ""
            val listaPromociones = dataSnapshot.child("listaPromociones").getValue(object : GenericTypeIndicator<MutableList<String?>>() {})

            UserPartner(nombre, cuil, correo, id, listaPromociones)
        } else {
            Log.d("DB - ID", "El usuario es NULO")
            null // El usuario no existe
        }
    }

    // VER COMO HACER QUE ANDE, COMPLICACIONES CON EL SUSPEND
    suspend fun obtenerPromocionPorId(id: String): Promocion? {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Promocion").child(id).get().await()

        if (dataSnapshot.exists()) {
            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val key = dataSnapshot.key
            val categoria = dataSnapshot.child("categoria").getValue(String::class.java)
            val comercio = dataSnapshot.child("comercio").getValue(String::class.java)
            val cuotas = dataSnapshot.child("cuotas").getValue(String::class.java)
            val dias = dataSnapshot.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val porcentaje = dataSnapshot.child("porcentaje").getValue(String::class.java)
            val proveedor = dataSnapshot.child("proveedor").getValue(String::class.java)
            val sucursales = dataSnapshot.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val tarjetas = dataSnapshot.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val tipoPromocion = dataSnapshot.child("tipoPromocion").getValue(String::class.java)
            val titulo = dataSnapshot.child("titulo").getValue(String::class.java)
            val topeNro = dataSnapshot.child("topeNro").getValue(String::class.java)
            val topeTexto = dataSnapshot.child("topeTexto").getValue(String::class.java)
            val tyc = dataSnapshot.child("tyc").getValue(String::class.java)
            val descripcion = dataSnapshot.child("descripcion").getValue(String::class.java)
            val url = dataSnapshot.child("url").getValue(String::class.java)

            val vigenciaDesdeString: String? = dataSnapshot.child("vigenciaDesde").getValue(String::class.java)
            val vigenciaHastaString: String? = dataSnapshot.child("vigenciaHasta").getValue(String::class.java)

            val vigenciaDesde = vigenciaDesdeString?.let { LocalDate.parse(it, formato) }
            val vigenciaHasta = vigenciaHastaString?.let { LocalDate.parse(it, formato) }

            val coroutineScope = CoroutineScope(Dispatchers.Main)
            var logo: String? = ""

            coroutineScope.launch {
                try {
                    if(comercio != null){
                        logo = Funciones().traerLogoComercio(comercio)}
                    else{logo = ""}


                } catch (e: Exception) {
                    println("Error al obtener promociones: ${e.message}")
                }
            }

            Log.d("DB - obtenerPromocionPorId", "${ logo }")

            val estado = dataSnapshot.child("estado").getValue(String::class.java)

            val promocion = Promocion(key, categoria, comercio, cuotas, dias, porcentaje, proveedor, sucursales,mutableListOf(), tarjetas,
                tipoPromocion, titulo, topeNro, topeTexto, tyc,descripcion, url, vigenciaDesde, vigenciaHasta,estado)


            return promocion
        } else {
            return null
        }
    }

    suspend fun obtenerTarjetaPorId(id: String): Tarjeta? {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Tarjeta").child(id).get().await()

        if (dataSnapshot.exists()) {
            val key = dataSnapshot.key
            val segmento = dataSnapshot.child("segmento").getValue(String::class.java)
            val entidad = dataSnapshot.child("entidad").getValue(String::class.java)
            val procesadora = dataSnapshot.child("procesadora").getValue(String::class.java)
            val tipoTarjeta = dataSnapshot.child("tipoTarjeta").getValue(String::class.java)
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val tarjeta = Tarjeta(key,procesadora,segmento,tipoTarjeta,entidad)
            return tarjeta
        } else {
            return null
        }
    }

    suspend fun obtenerSucursalPorId(id: String): Sucursal? {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Sucursal").child(id).get().await()

        if (dataSnapshot.exists()) {
            var latitud = dataSnapshot.child("latitud").getValue(String::class.java)
            if (latitud != null) {
                if(latitud.contains("posee")|| latitud.contains("Error")){
                    latitud = "0.1"
                }
            }
            var longitud = dataSnapshot.child("longitud").getValue(String::class.java)
            if (longitud != null) {
                if(longitud.contains("posee") || longitud.contains("Error")){
                    longitud = "0.1"
                }
            }
            val key = dataSnapshot.key
            val direccion = dataSnapshot.child("direccion").getValue(String::class.java)
            val idComercio = dataSnapshot.child("idComercio").getValue(String::class.java)
            val sucursal = Sucursal(key,direccion,idComercio,latitud?.toDouble(),longitud?.toDouble())
            return sucursal
        } else {
            return null
        }
    }
}


//EJEMPLOS LLAMADOS:
/*

       val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val resultados = instancia.obtenerIdsSinc("Comercio", "categoria", "Gastronomía")
                if (resultados != null) {
                    for (resultado in resultados){
                        Log.d("Resultado", resultado)
                    }
                }
                else{
                   Log.d("Resultado","NULO")
                }
            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }


LeeridUnico:
        var instancia = leerId()
        instancia.leerIdUnico("Comercio","nombre","Aerolíneas Plus"){ id ->
            if (id != null) {
                Log.d("tag","El ID único del comercio' es: $id")
            } else {
                Log.d("tag","No se encontró ningún comercio")
            }
        }
        println("Usuarios escritos en Firebase Realtime Database.")

LeerListaDeId:
        var instancia = leerId()
        instancia.leerListaIds("Comercio","categoria","Otros"){ listaIDs ->
            if (listaIDs.isNotEmpty()) {
                Log.d("MiTag", "IDs únicos de comercios': $listaIDs")
            } else {
                Log.d("MiTag", "No se encontraron comercios")
            }
        }

 */
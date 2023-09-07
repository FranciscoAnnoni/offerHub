package com.example.offerhub

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class leerId {

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
                Log.d("Error","Error en lectura de bd")
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
                Log.d("Error","Error en lectura de bd")
            }
        })
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario? = withContext(Dispatchers.IO) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Usuario").child(id).get().await()

        if (dataSnapshot.exists()) {
            val correo = dataSnapshot.child("correo").getValue(String::class.java)
            val contraseina = dataSnapshot.child("contraseina").getValue(String::class.java)?: ""
            val nombre = dataSnapshot.child("nombre").getValue(String::class.java) ?: ""
            val tarjetas = dataSnapshot.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val favoritos = dataSnapshot.child("favoritos").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val wishlistComercio = dataSnapshot.child("wishlistComercio").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val wishlistRubro = dataSnapshot.child("wishlistRubro").getValue(object : GenericTypeIndicator<List<String?>>() {})
            val promocionesReintegro = dataSnapshot.child("promocionesReintegro").getValue(object : GenericTypeIndicator<List<String?>>() {})

            Usuario(id, nombre, correo, contraseina, tarjetas, favoritos, wishlistComercio, wishlistRubro, promocionesReintegro)
        } else {
            null // El usuario no existe
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
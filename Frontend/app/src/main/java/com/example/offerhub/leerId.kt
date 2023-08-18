package com.example.offerhub

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class leerId {
    fun leerIdUnico(tabla: String,campoFiltro: String,valorFiltro: String,callback: (String?) -> Unit){
        val database = FirebaseDatabase.getInstance("https://oh-backend-848a1-default-rtdb.firebaseio.com/")
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
        val database = FirebaseDatabase.getInstance("https://oh-backend-848a1-default-rtdb.firebaseio.com/")
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

}


//EJEMPLOS LLAMADOS:
/*

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
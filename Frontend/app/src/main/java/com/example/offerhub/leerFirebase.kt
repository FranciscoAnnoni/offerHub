package com.example.offerhub

import com.google.firebase.database.ValueEventListener

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.offerhub.ui.theme.OfferHubTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class LecturaBD {
    //val listaProv: MutableList<String> = mutableListOf()

    fun obtenerPromocionesConCategoria(categoria: String, callback: (MutableList<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com/")
        val promocionRef = database.getReference("/Promocion")
        val lista: MutableList<String> = mutableListOf()
        promocionRef.orderByChild("categoria").equalTo(categoria).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val titulo = data.child("titulo").getValue(String::class.java)
                        Log.d("Promocion", "Titulo: $titulo")
                        if (titulo != null) {
                            lista.add(titulo)
                        }
                    }

                }
                callback.invoke(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(lista)
            }
        })
    }

    fun obtenerEntidadesPorNombre(nombre: String, callback: (String) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val entidadesRef = database.getReference("/Entidad")

        entidadesRef.orderByChild("nombre").equalTo(nombre).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val entidadSnapshot = dataSnapshot.children.first()
                    val tipo = entidadSnapshot.child("tipo").getValue(String::class.java)
                    if (tipo != null) {
                        callback.invoke(tipo)
                    } else {
                        callback.invoke("Vacio")
                    }
                } else {
                    callback.invoke("Vacio")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke("Vacio")
            }
        })
    }


}



//EJEMPLOS LLAMADAS A FUNCIONES
/*
        var instancia = LecturaBD()


        setContentView(R.layout.activity_main)

        val textViewNombre = findViewById<TextView>(R.id.textViewNombre)
        textViewNombre.text = "Nombre: $nombre"
        val textViewTipo = findViewById<TextView>(R.id.textViewTipo)
        instancia.obtenerEntidadesPorNombre(nombre){tipo -> textViewTipo.text = "Tipo: $tipo"}

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        instancia.obtenerPromocionesConCategoria("Entretenimiento") { list ->
            //que hacer con la lista
        }


 */
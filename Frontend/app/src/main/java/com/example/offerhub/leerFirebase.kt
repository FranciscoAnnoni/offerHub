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

class Entidad{
    // Propiedades (atributos) de la clase
    var nombre: String?
    var tipo: String?

    // Constructor primario
    constructor(nombre: String?, tipo: String?) {
        this.nombre = nombre
        this.tipo = tipo
    }
}
class LecturaBD {

    fun leerBdString(tabla: String,campoFiltro: String,valorFiltro: String,campoRetorno: String,callback: (MutableList<String>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com/")
        val promocionRef = database.getReference("/$tabla")
        val lista: MutableList<String> = mutableListOf()
        promocionRef.orderByChild("$campoFiltro").equalTo(valorFiltro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val dato = data.child("$campoRetorno").getValue(String::class.java)

                        if (dato != null) {
                            lista.add(dato)
                        }
                    }

                }
                callback.invoke(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error","Error en lectura de bd")
            }
        })
    }

    fun <T> leerBdClase(tabla: String,campoFiltro: String,valorFiltro: String,callback: (MutableList<T>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com/")
        Log.d("prueba",tabla)
        val promocionRef = database.getReference("/$tabla")

        val lista: MutableList<T> = mutableListOf()
        promocionRef.orderByChild("$campoFiltro").equalTo(valorFiltro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children){
                            when (tabla) {
                                "Entidad" ->{
                                    val instancia = Entidad(data.child("nombre").getValue(String::class.java),  data.child("tipo").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                               // "tablaB" -> ClaseB("NombreB", "TipoB")
                                else -> throw IllegalArgumentException("Tabla desconocida")
                            }
                           // val instancia = Entidad(nombre, tipo)

                        }
                    }

                callback.invoke(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error","Error en lectura de bd")
            }
        })
    }
}



//EJEMPLOS LLAMADAS A FUNCIONES
/*
LeerBdString:
        var instancia = LecturaBD()
        val lista: MutableList<String> = mutableListOf()
        setContentView(R.layout.activity_main)
        instancia.leerBdString("Promocion","categoria","Gastronomía","titulo"){list -> lista.addAll(list)}
        for (data in lista){
        Log.d("Promocion", "titulo: $data")
        }


leerBdClase()
        var instancia = LecturaBD()

        setContentView(R.layout.activity_main)
        instancia.leerBdClase<Entidad>("Entidad","tipo","Bancaria"){list ->
            for (item in list) {
                when (item) {
                    is Entidad -> println("Instancia de Entidad: ${item.nombre}")
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

 */
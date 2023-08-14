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

    fun leerBdString(tabla: String,campoFiltro: String,valorFiltro: String,campoRetorno: String,callback: (MutableList<String>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com/")
        Log.d("prueba",tabla)
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
}



//EJEMPLOS LLAMADAS A FUNCIONES
/*
        var instancia = LecturaBD()
        val lista: MutableList<String> = mutableListOf()
        setContentView(R.layout.activity_main)
        instancia.leerBdString("Promocion","categoria","Gastronomía","titulo"){list -> lista.addAll(list)}
        for (data in lista){
        Log.d("Promocion", "titulo: $data")
        }


 */
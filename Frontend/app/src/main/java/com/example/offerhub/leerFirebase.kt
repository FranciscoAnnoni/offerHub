package com.example.offerhub


    import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.offerhub.ui.theme.OfferHubTheme
import com.google.firebase.database.FirebaseDatabase

class Promocion {
    val listaProv: MutableList<String> = mutableListOf()

    fun obtenerPromocionesConCategoria(categoria: String) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com/")
        val promocionRef = database.getReference("Promocion")

        var query = promocionRef.orderByChild("categoria").equalTo(categoria)
        var proveedor: String
        query.get().addOnSuccessListener { dataSnapshot ->
            for (promoSnapshot in dataSnapshot.children) {
                proveedor = promoSnapshot.child("proveedor").getValue(String::class.java).toString()
                println(proveedor)
            }
        }
    }

}

fun main(){
    val intancia = Promocion()
    intancia.obtenerPromocionesConCategoria("Juguetes")

}


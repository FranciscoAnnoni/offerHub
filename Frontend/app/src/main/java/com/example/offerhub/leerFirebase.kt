package com.example.offerhub

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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

class Comercio{
    // Propiedades (atributos) de la clase
    var categoria: String?
    var nombre: String?
    var logo: String?

    // Constructor primario
    constructor(nombre: String?, categoria: String?,logo:String?) {
        this.nombre = nombre
        this.categoria = categoria
        this.logo =  logo
    }

    //Funcion que convierte de base64 a bitmap para luego poder mostrar como imagen
    fun base64ToBitmap(logo: String?): Bitmap? {
        if (logo.isNullOrBlank()) {
            return null
        }

        val decodedBytes = Base64.decode(logo, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

   //Desde codigo xml con id: imageView se llamaria de la siguiente forma:
    //val bitmap = base64ToBitmap(base64Image)
    //if (bitmap != null) {
    //    imageView.setImageBitmap(bitmap)
    //}



}

class Tarjeta{
    // Propiedades (atributos) de la clase
    var entidad: String?
    var procesadora: String?
    var segmento: String?
    var tipoTarjeta: String?

    //   var logo?????

    // Constructor primario
    constructor(procesadora: String?, segmento: String?,tipoTarjeta: String?,entidad: String?) {
        this.procesadora = procesadora
        this.segmento = segmento
        this.tipoTarjeta = tipoTarjeta
        this.entidad = entidad
    }
}

class Sucursal{
    // Propiedades (atributos) de la clase
    var direccion: String?
    var idComercio: String?
    var latitud: Double?
    var longitud: Double?

    //   var logo?????

    // Constructor primario
    constructor(direccion: String?, idComercio: String?,latitud: Double?,longitud: Double?) {
        this.direccion = direccion
        this.idComercio = idComercio
        this.latitud = latitud
        this.longitud = longitud
    }
}

class Promocion{
    // Propiedades (atributos) de la clase
    var categoria: String?
    var comercio: String?
    val dias: List<String?>
    val tarjetas: List<String?>
    val proveedor: String?
    val titulo: String?
    val tope: String?
    val tyc: String?
    val url: String?
    val vigenciaDesde: LocalDate?
    val vigenciaHasta: LocalDate?

    //   var logo?????

    // Constructor primario
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(categoria: String?, comercio: String?, dias: List<String?>, tarjetas: List<String?>,
                proveedor: String?, titulo: String?, tope: String?, tyc: String?, url: String?, vigenciaDesde: String?,
                vigenciaHasta: String?) {
        this.categoria = categoria
        this.comercio = comercio
        this.dias = dias
        this.tarjetas = tarjetas
        this.proveedor=proveedor
        this.titulo=titulo
        this.tope=tope
        this.tyc =tyc
        this.url=url
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        this.vigenciaDesde = vigenciaDesde?.let {
            LocalDate.parse(it, formato)
        }
        this.vigenciaHasta = vigenciaHasta?.let {
            LocalDate.parse(it, formato)
        }
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
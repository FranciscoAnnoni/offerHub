package com.example.offerhub.funciones

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.offerhub.Comercio
import com.example.offerhub.EscribirBD
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.PromocionEscritura
import com.example.offerhub.SucursalEscritura
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FuncionesPartners {
    fun aprobarPromocion(promocion: Promocion) {
        val instancia = EscribirBD()
        promocion.id?.let {
            instancia.editarAtributoDeClase(
                "/Promocion",
                it,
                "estado",
                "Aprobado"
            )
        }
    }
    suspend fun  obtenerPromosPorComercio(comercioparam: String): List<Promocion> = suspendCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/Promocion")
        val lista: MutableList<Promocion> = mutableListOf()
        promocionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val comercio: String? = data.child("comercio").getValue(String::class.java)
                        if (comercio == comercioparam) {
                            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val desdeFormateado: LocalDate?
                            val hastaFormateado: LocalDate?
                            var vigenciaDesdeString = data.child("vigenciaDesde").getValue(String::class.java)
                            if(vigenciaDesdeString != "No posee" && vigenciaDesdeString != " - "){
                                desdeFormateado = LocalDate.parse(vigenciaDesdeString, formato)
                            } else { desdeFormateado = null }
                            var vigenciaHastaString: String? = data.child("vigenciaHasta").getValue(String::class.java)
                            if(vigenciaHastaString != "No posee" && vigenciaHastaString != " - "){
                                hastaFormateado = LocalDate.parse(vigenciaHastaString, formato)
                            } else { hastaFormateado = null }
                            val coroutineScope = CoroutineScope(Dispatchers.Main)
                            val instancia = Promocion(data.key,data.child("categoria").getValue(String::class.java),  comercio,
                                data.child("cuotas").getValue(String::class.java),
                                data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("porcentaje").getValue(String::class.java), data.child("proveedor").getValue(String::class.java),
                                data.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tipoPromocion").getValue(String::class.java),
                                data.child("titulo").getValue(String::class.java), data.child("topeNro").getValue(String::class.java),
                                data.child("topeTexto").getValue(String::class.java),data.child("tyc").getValue(String::class.java),data.child("descripcion").getValue(String::class.java),
                                data.child("url").getValue(String::class.java),
                                desdeFormateado,
                                hastaFormateado,
                                data.child("estado").getValue(String::class.java),
                                data.child("motivo").getValue(String::class.java))
                            lista.add(instancia)
                        }
                        }
                    }

                    continuation.resume(lista)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("Error", "Error en lectura de bd")
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }

    fun rechazarPromocion(promocion: Promocion, razon: String) {
        val instancia = EscribirBD()
        promocion.id?.let {
            instancia.editarAtributoDeClase(
                "/Promocion",
                it,
                "estado",
                "Rechazado"
            )
        }
        promocion.id?.let { instancia.editarAtributoDeClase("/Promocion", it, "motivo", razon) }
    }

    fun registrarComercio(comercio: Comercio, sucursales: List<SucursalEscritura>?): String? {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referenciaCom: DatabaseReference = database.reference.child("/Comercio")

        val comercioReferencia = referenciaCom.push()
        comercioReferencia.setValue(comercio)
        return comercioReferencia.key
    }

    fun agregarSucursalAComercio(idComercio: String, sucursal: String) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference("/Comercio").child(idComercio).child("sucursales")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listaSucursales = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val valor = snapshot.getValue(String::class.java)
                    if (valor != null) {
                        listaSucursales.add(valor)
                    }
                }

                val nuevoIndice = listaSucursales.size.toString()
                referencia.child(nuevoIndice).setValue(sucursal)
                    .addOnCompleteListener {}
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun eliminarSucursalDeComercio(idComercio: String, sucursal: String) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference("/Comercio").child(idComercio).child("sucursales")

        referencia.orderByValue().equalTo(sucursal).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo básico de error: Imprimir el mensaje de error en la consola
                println("Operación cancelada. Error: ${error.message}")
            }
        })
    }


    suspend fun validarComercioNuevo(comercio: Comercio): Boolean {
        var instancia = LeerId()
        val resultado = comercio.cuil?.let { instancia.obtenerIdSinc("Comercio", "cuil", it) }
        return resultado == null
    }


    fun escribirPromocion(promocion: PromocionEscritura) {

        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Promocion")

        val promoReferencia = referencia.push()
        promoReferencia.setValue(promocion)
    }

    suspend fun obtenerPromosPendientes(): List<Promocion> = suspendCoroutine { continuation ->
        val database =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/Promocion")
        val lista: MutableList<Promocion> = mutableListOf()
        promocionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        val estado: String? = data.child("estado").getValue(String::class.java)
                        if (estado == "pendiente") {
                            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val desdeFormateado: LocalDate?
                            val hastaFormateado: LocalDate?
                            var vigenciaDesdeString =
                                data.child("vigenciaDesde").getValue(String::class.java)
                            if (vigenciaDesdeString != "No posee" && vigenciaDesdeString != " - ") {
                                desdeFormateado = LocalDate.parse(vigenciaDesdeString, formato)
                            } else {
                                desdeFormateado = null
                            }
                            var vigenciaHastaString: String? =
                                data.child("vigenciaHasta").getValue(String::class.java)
                            if (vigenciaHastaString != "No posee" && vigenciaHastaString != " - ") {
                                hastaFormateado = LocalDate.parse(vigenciaHastaString, formato)
                            } else {
                                hastaFormateado = null
                            }
                            val coroutineScope = CoroutineScope(Dispatchers.Main)
                            val instancia = Promocion(
                                data.key,
                                data.child("categoria").getValue(String::class.java),
                                data.child("comercio").getValue(String::class.java),
                                data.child("cuotas").getValue(String::class.java),
                                data.child("dias")
                                    .getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("porcentaje").getValue(String::class.java),
                                data.child("proveedor").getValue(String::class.java),
                                data.child("sucursales")
                                    .getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tarjetas")
                                    .getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tipoPromocion").getValue(String::class.java),
                                data.child("titulo").getValue(String::class.java),
                                data.child("topeNro").getValue(String::class.java),
                                data.child("topeTexto").getValue(String::class.java),
                                data.child("tyc").getValue(String::class.java),
                                data.child("descripcion").getValue(String::class.java),
                                data.child("url").getValue(String::class.java),
                                desdeFormateado,
                                hastaFormateado,
                                estado,
                                data.child("motivo").getValue(String::class.java)
                            )
                            lista.add(instancia)
                        }

                    }
                }

                continuation.resume(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Error en lectura de bd")
                continuation.resumeWithException(databaseError.toException())
            }
        })
    }

    suspend fun obtenerReportes(): List<String> = suspendCoroutine { continuation ->
        val database =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/Reportes")
        val lista: MutableList<String> = mutableListOf()
        promocionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        val reporte = data.getValue(String::class.java)
                        if (reporte != null) {
                            lista.add(reporte)
                        }
                    }
                }
                continuation.resume(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Error en lectura de bd")
                continuation.resumeWithException(databaseError.toException())
            }
        })
    }
}

/*
ESCRIBIR PROMOCION:
val dias: List<String?> = listOf("Lunes")
val sucursales: List<String?> = listOf("almagro")
val tarjetas: List<String?> = listOf("No posee")
var promo1 = PromocionEscritura("Prueba3","Offerhub","3",dias,"30","jp",sucursales,tarjetas,"descuento","promopiola","2000","dos mil","ninguno","hola.com","2020-03-03","2025-01-01","pendiente")
var instancia = FuncionesPartners()
instancia.escribirPromocion(promo1)


REGISTRAR COMERCIO
val coroutineScope = CoroutineScope(Dispatchers.Main)
var cuil = 2040420213
val instancia = FuncionesPartners()
val comercio1= Comercio(null,"pepitos","otros",null,cuil.toString())
val sucursal = SucursalEscritura(null,"Julian Alvarez",null,"30.0","20.0")
val lista = listOf(sucursal)
coroutineScope.launch {
    if(instancia.validarComercioNuevo(comercio1)){
        instancia.registrarComercio(comercio1,lista)
    }
    else{
        Log.d("Comercio existente","El comercio que se trata de escribir ya se encuentra registrado en el sistema")
    }
}


APROBAR Y RECHAZAR PROMO:

        val dias: List<String?> = listOf()
        val sucursales: List<String?> = listOf()
        val tarjetas: List<String?> = listOf()
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val vigencia1 = LocalDate.parse("2020-01-01", formato)
        val promo1 = Promocion("-NemuPn8TUEpxDWYeqxN","null","null","null",dias,"null","null",sucursales,tarjetas,"null","null","null","null","null","null","null",vigencia1,vigencia1,"null","null")
        val promo2 = Promocion("-NemurS5z7CptbxcJInp","null","null","null",dias,"null","null",sucursales,tarjetas,"null","null","null","null","null","null","null",vigencia1,vigencia1,"null","null")
        instancia.aprobarPromocion(promo1)
        instancia.rechazarPromocion(promo2,"Es una promocion que no me gusta")
 */
package com.example.offerhub.funciones

import android.util.Log
import com.example.offerhub.Comercio
import com.example.offerhub.EscribirBD
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.PromocionEscritura
import com.example.offerhub.SucursalEscritura
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FuncionesPartners{
    fun aprobarPromocion(promocion:Promocion){
        val instancia = EscribirBD()
        promocion.id?.let { instancia.editarAtributoDeClase("/Promocion", it,"estado","Aprobado") }
    }

    fun rechazarPromocion(promocion:Promocion,  razon:String){
        val instancia = EscribirBD()
        promocion.id?.let { instancia.editarAtributoDeClase("/Promocion", it,"estado","Rechazado") }
        promocion.id?.let { instancia.editarAtributoDeClase("/Promocion", it,"nota",razon) }
    }

    fun registrarComercio(comercio: Comercio, sucursales:List<SucursalEscritura>?): String? {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referenciaCom: DatabaseReference = database.reference.child("/Comercio")

        val comercioReferencia = referenciaCom.push()
        comercioReferencia.setValue(comercio)
        if (sucursales != null){
            val referenciaSuc: DatabaseReference = database.reference.child("/Sucursal")
            for (sucursal in sucursales){
                sucursal.idComercio = comercioReferencia.key
                val sucursalReferencia = referenciaSuc.push()
                sucursalReferencia.setValue(sucursal)
            }
        }
        return comercioReferencia.key
    }

    suspend fun validarComercioNuevo(comercio: Comercio): Boolean {
        var instancia = LeerId()
        val resultado = comercio.cuil?.let { instancia.obtenerIdSinc("Comercio", "cuil", it) }
        return resultado == null
    }

    suspend fun agregarSucursal(cuilComercio:String,sucursal: SucursalEscritura) {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referenciaSuc: DatabaseReference = database.reference.child("/Sucursal")
        var instancia = LeerId()
        val resultado = instancia.obtenerIdSinc("Comercio", "cuil",cuilComercio)
        if (resultado!=null){
            sucursal.idComercio=resultado
            val sucursalReferencia = referenciaSuc.push()
            sucursalReferencia.setValue(sucursal)
        }else{
            Log.d("Comercio inexistente","El cuil del comercio aun no se encuentra registrado o no fue encontrado")
        }
    }

    fun escribirPromocion(promocion: PromocionEscritura) {

        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Promocion")

        val promoReferencia = referencia.push()
        promoReferencia.setValue(promocion)
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
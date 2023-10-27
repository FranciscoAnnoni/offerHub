package com.example.offerhub
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.offerhub.data.User
import com.example.offerhub.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SucursalEscritura{
    // Propiedades (atributos) de la clase
    var direccion: String?
    var idComercio: String?
    var latitud: String?
    var longitud: String?


    // Constructor primario
    constructor(id:String?,direccion: String?, idComercio: String?,latitud: String?,longitud: String?) {
        this.direccion = direccion
        this.idComercio = idComercio
        this.latitud = latitud
        this.longitud = longitud
    }
}
class PromocionEscritura {
    // Propiedades (atributos) de la clase
    var categoria: String?
    var comercio: String?
    var cuotas: String?
    val dias: List<String?>?
    var porcentaje: String?
    var proveedor: String?
    val sucursales: List<String?>?
    val tarjetas: List<String?>?
    val tipoPromocion: String?
    var titulo: String?
    var topeNro: String?
    val topeTexto: String?
    val tyc: String?
    val url: String?
    val descripcion: String?
    var vigenciaDesde: String?
    var vigenciaHasta: String?
    val estado: String?
    val motivo: String?


    // Constructor primario

    constructor(
        categoria: String?,
        comercio: String?,
        cuotas: String?,
        dias: List<String?>?,
        porcentaje: String?,
        proveedor: String?,
        sucursales: List<String?>?,
        tarjetas: List<String?>?,
        tipoPromocion: String?,
        titulo: String?,
        topeNro: String?,
        topeTexto: String?,
        tyc: String?,
        url: String?,
        descripcion: String?,
        vigenciaDesde: String?,
        vigenciaHasta: String?,
        estado: String?,
        motivo: String? = null
    ) {
        this.categoria = categoria
        this.comercio = comercio
        this.cuotas = cuotas
        this.dias = dias
        this.porcentaje = porcentaje
        this.proveedor = proveedor
        this.sucursales = sucursales
        this.tarjetas = tarjetas
        this.tipoPromocion = tipoPromocion
        this.titulo = titulo
        this.topeNro = topeNro
        this.topeTexto = topeTexto
        this.tyc = tyc
        this.url = url
        this.descripcion = descripcion
        this.vigenciaDesde = vigenciaDesde
        this.vigenciaHasta = vigenciaHasta
        this.estado = estado
        this.motivo = motivo
    }

    fun validar(): List<MutableList<String>> {
        var error= mutableListOf<String>()
        var campos= mutableListOf<String>()
        if (this.titulo==null || this.titulo!!.isEmpty()) {
            error.add("Título requerido")
            campos.add("errorTitulo")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val fechaActual = Date()
        if(this.vigenciaHasta==null || this.vigenciaHasta!!.isNotEmpty()) {
            val fechaHastaDate = dateFormat.parse(this.vigenciaHasta)
            if (this.vigenciaDesde != null && this.vigenciaDesde!!.isNotEmpty()){
                if(fechaHastaDate <= dateFormat.parse(this.vigenciaDesde)){
                        error.add("Fecha Hasta debe ser posterior a la Fecha Desde.")
                        campos.add("errorVigencia")
                }
            }


            if (fechaHastaDate <= fechaActual) {
                error.add("Fecha Hasta debe ser mayor a la Fecha Actual.")
                campos.add("errorVigencia")
            }
        } else {
            error.add("Fecha Hasta no puede estar vacio.")
            campos.add("errorVigencia")
        }
        if (this.dias == null || this.dias.size==0) {
            error.add("Al menos un Día de Validez debe ser seleccionado.")
            campos.add("errorDias")
        }
        if (this.tipoPromocion != null && this.tipoPromocion.isNotEmpty()) {

            if (tipoPromocion == "Cuotas") {
                if (cuotas==null || cuotas!!.isEmpty()) {
                    error.add("Cantidad de cuotas no puede estar vacio.")
                    campos.add("errorTipoPromo")
                }
            } else if (tipoPromocion == "Reintegro" || tipoPromocion == "Descuento") {
                if (porcentaje==null || porcentaje!!.isEmpty()) {
                    error.add("Porcentaje de Descuento no puede estar vacio.")
                    campos.add("errorTipoPromo")
                }
            }
        } else {
            error.add("Tipo de Promoción debe ser seleccionado.")
            campos.add("errorTipoPromo")
        }
        return listOf<MutableList<String>>(campos,error)
    }
}

class Usuario{
    // Propiedades (atributos) de la clase
    var id: String
    var nombre: String
    var correo: String?
    var tarjetas: MutableList<String?>?
    var favoritos: MutableList<String?>?
    var wishlistComercio: MutableList<String?>?
    var wishlistRubro: MutableList<String?>?
    var promocionesReintegro: MutableList<String?>?
    var homeModoFull: String?

    // Constructor primario
    constructor(id: String, nombre: String, correo: String?, tarjetas: MutableList<String?>?, favoritos: MutableList<String?>?, wishlistComercio: MutableList<String?>?,  wishlistRubro: MutableList<String?>?, promocionesReintegro: MutableList<String?>?,homeModoFull: String?){
        this.id = id
        this.nombre = nombre
        this.correo = correo
        this.tarjetas = tarjetas
        this.favoritos = favoritos
        this.wishlistComercio = wishlistComercio
        this.wishlistRubro = wishlistRubro
        this.promocionesReintegro = promocionesReintegro
        this.homeModoFull=homeModoFull
    }
}



class EscribirBD {
    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register


    fun escribirUsuarioEnFirebase(usuario: Usuario, user: User) {

        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Usuario")

            val usuarioReferencia = referencia.push()
            usuarioReferencia.setValue(usuario).addOnSuccessListener {
                _registrationSuccess.value = true // Registro exitoso
                _register.value = Resource.Success(user)
            }.addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }

    }
    
    fun escribirPromocion(promocion: PromocionEscritura) {

        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Promocion")

        val promoReferencia = referencia.push()
        promoReferencia.setValue(promocion)
    }

    fun escribirUsuariosEnFirebase(usuarios: List<Usuario>) {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Usuario")

        usuarios.forEach { usuario ->
            val usuarioReferencia = referencia.push()
            usuarioReferencia.setValue(usuario)
        }
    }

//    A PARTIR DE ACA SIRVE PARA EDITAR LOS OBJETOS DE LA BASE DE DATOS

    fun agregarElementoAListas(userId: String, elementoId: String, clase: String, nombreLista: String) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference(clase).child(userId).child(nombreLista)

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listaFavoritos = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val valor = snapshot.getValue(String::class.java)
                    if (valor != null) {
                        listaFavoritos.add(valor)
                    }
                }

                val nuevoIndice = listaFavoritos.size.toString()
                referencia.child(nuevoIndice).setValue(elementoId)
                    .addOnCompleteListener {}
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun eliminarElementoDeListas(userId: String, elementoId: String, clase: String, nombreLista: String) {

        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referenciaFavoritos = database.getReference(clase).child(userId).child(nombreLista)

        referenciaFavoritos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val valor = snapshot.getValue(String::class.java)
                    if (valor == elementoId) {
                        snapshot.ref.removeValue()
                            .addOnCompleteListener {}
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun editarAtributoDeClase(nombreClase: String, idObjeto: String, atributo: String, valorNuevo: String){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference(nombreClase).child(idObjeto).child(atributo)
        referencia.setValue(valorNuevo).addOnCompleteListener {}
    }

}
//Ejemplo uso:
/*
        var instancia = EscribirBD()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        val usuariosNuevos = listOf(
            Usuario("Adam Bareiro", "adam9@gmail.com", "carlitos", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro),
            Usuario("Nahuel Barrios", "perritogambeta@hotmail.com", "enanito",tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)
        )
        instancia.escribirUsuariosEnFirebase(usuariosNuevos)



 */
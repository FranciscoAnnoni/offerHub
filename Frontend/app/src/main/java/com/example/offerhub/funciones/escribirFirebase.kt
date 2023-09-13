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
    val titulo: String?
    val topeNro: String?
    val topeTexto: String?
    val tyc: String?
    val url: String?
    val vigenciaDesde: String?
    val vigenciaHasta: String?
    val estado: String?

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
        vigenciaDesde: String?,
        vigenciaHasta: String?,
        estado: String?
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
        this.vigenciaDesde = vigenciaDesde
        this.vigenciaHasta = vigenciaHasta
        this.estado = estado
    }
}

class Usuario{
    // Propiedades (atributos) de la clase
    var id: String
    var nombre: String
    var correo: String?
    var tarjetas: List<String?>?
    var favoritos: List<String?>?
    var wishlistComercio: List<String?>?
    var wishlistRubro: List<String?>?
    var promocionesReintegro: List<String?>?

    // Constructor primario
    constructor(id: String, nombre: String, correo: String?, tarjetas: List<String?>?, favoritos: List<String?>?, wishlistComercio: List<String?>?,  wishlistRubro: List<String?>?, promocionesReintegro: List<String?>?){
        this.id = id
        this.nombre = nombre
        this.correo = correo
        this.tarjetas = tarjetas
        this.favoritos = favoritos
        this.wishlistComercio = wishlistComercio
        this.wishlistRubro = wishlistRubro
        this.promocionesReintegro = promocionesReintegro
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

    fun registrarComercio(comercio:Comercio,sucursales:List<SucursalEscritura>) {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referenciaCom: DatabaseReference = database.reference.child("/Comercio")

        val comercioReferencia = referenciaCom.push()
        comercioReferencia.setValue(comercio)

        val referenciaSuc: DatabaseReference = database.reference.child("/Sucursal")
        for (sucursal in sucursales){
            sucursal.idComercio = comercioReferencia.key
            val sucursalReferencia = referenciaSuc.push()
            sucursalReferencia.setValue(sucursal)
        }
    }

    suspend fun validarComercioNuevo(comercio:Comercio): Boolean {
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


ESCRIBIR PROMOCION:
        val dias: List<String?> = listOf("Lunes")
        val sucursales: List<String?> = listOf("almagro")
        val tarjetas: List<String?> = listOf("No posee")
        var promo1 = PromocionEscritura("Prueba3","Offerhub","3",dias,"30","jp",sucursales,tarjetas,"descuento","promopiola","2000","dos mil","ninguno","hola.com","2020-03-03","2025-01-01","pendiente")
        var instancia = EscribirBD()
        instancia.escribirPromocion(promo1)


REGISTRAR COMERCIO
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var cuil = 2040420213
        val instancia = EscribirBD()
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
 */
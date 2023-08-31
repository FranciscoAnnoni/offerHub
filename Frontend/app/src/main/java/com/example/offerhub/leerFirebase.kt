package com.example.offerhub
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.google.firebase.database.ValueEventListener
import org.threeten.bp.format.DateTimeFormatter
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import org.threeten.bp.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Entidad{
    // Propiedades (atributos) de la clase
    var id: String?
    var nombre: String?
    var tipo: String?

    // Constructor primario
    constructor(id:String?,nombre: String?, tipo: String?) {
        this.id = id
        this.nombre = nombre
        this.tipo = tipo
    }
}

class Comercio{
    // Propiedades (atributos) de la clase
    var id:String?
    var categoria: String?
    var nombre: String?
    var logo: String?

    // Constructor primario
    constructor(id:String?,nombre: String?, categoria: String?,logo:String?) {
        this.id = id
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
    var id: String?
    var entidad: String?
    var procesadora: String?
    var segmento: String?
    var tipoTarjeta: String?


    // Constructor primario
    constructor(id:String?,procesadora: String?, segmento: String?,tipoTarjeta: String?,entidad: String?) {
        this.id = id
        this.procesadora = procesadora
        this.segmento = segmento
        this.tipoTarjeta = tipoTarjeta
        this.entidad = entidad
    }
}

class Sucursal{
    // Propiedades (atributos) de la clase
    var id: String?
    var direccion: String?
    var idComercio: String?
    var latitud: Double?
    var longitud: Double?


    // Constructor primario
    constructor(id:String?,direccion: String?, idComercio: String?,latitud: Double?,longitud: Double?) {
        this.id = id
        this.direccion = direccion
        this.idComercio = idComercio
        this.latitud = latitud
        this.longitud = longitud
    }
}

class Promocion{
    // Propiedades (atributos) de la clase
    var id: String?
    var categoria: String?
    var comercio: String?
    val dias: List<String?>?
    val tarjetas: List<String?>?
    val proveedor: String?
    val titulo: String?
    val tope: String?
    val tyc: String?
    val url: String?
    val vigenciaDesde: LocalDate?
    val vigenciaHasta: LocalDate?

    // Constructor primario
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(id:String?,categoria: String?, comercio: String?, dias: List<String?>?, tarjetas: List<String?>?,
                proveedor: String?, titulo: String?, tope: String?, tyc: String?, url: String?, vigenciaDesde: LocalDate?,
                vigenciaHasta: LocalDate?) {
        this.id = id
        this.categoria = categoria
        this.comercio = comercio
        this.dias = dias
        this.tarjetas = tarjetas
        this.proveedor=proveedor
        this.titulo=titulo
        this.tope=tope
        this.tyc =tyc
        this.url=url
        this.vigenciaDesde = vigenciaDesde
        this.vigenciaHasta = vigenciaHasta
    }
}

class LecturaBD {

    fun leerBdString(tabla: String,campoFiltro: String,valorFiltro: String,campoRetorno: String,callback: (MutableList<String>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com")
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
        val database = FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com")
        val promocionRef = database.getReference("/$tabla")

        val lista: MutableList<T> = mutableListOf()
        promocionRef.orderByChild("$campoFiltro").equalTo(valorFiltro).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                        for (data in dataSnapshot.children){
                            when (tabla) {
                                "Entidad" ->{
                                    val instancia = Entidad(data.key,data.child("nombre").getValue(String::class.java),  data.child("tipo").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                                "Comercio" ->{
                                    val instancia = Comercio(data.key,data.child("nombre").getValue(String::class.java),  data.child("categoria").getValue(String::class.java),data.child("logo").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                                "Tarjeta" ->{
                                    val instancia = Tarjeta(data.key,data.child("procesadora").getValue(String::class.java),  data.child("segmento").getValue(String::class.java),data.child("tipoTarjeta").getValue(String::class.java),data.child("entidad").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                                "Sucursal" ->{
                                    val instancia = Sucursal(data.key,data.child("direccion").getValue(String::class.java),  data.child("idComercio").getValue(String::class.java),
                                        data.child("latitud").getValue(String::class.java)?.toDouble(),
                                        data.child("longitud").getValue(String::class.java)
                                            ?.toDouble()
                                    )
                                    lista.add(instancia as T)
                                }
                                "Promocion" ->{
                                    val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val vigenciaDesdeString: String? = data.child("vigenciaDesde").getValue(String::class.java)
                                    val vigenciaHastaString: String? = data.child("vigenciaHasta").getValue(String::class.java)
                                    val instancia = Promocion(data.key,data.child("categoria").getValue(String::class.java),  data.child("comercio").getValue(String::class.java),
                                        data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("proveedor").getValue(String::class.java),data.child("titulo").getValue(String::class.java),
                                        data.child("tope").getValue(String::class.java),data.child("tyc").getValue(String::class.java),
                                        data.child("url").getValue(String::class.java),vigenciaDesdeString?.let { LocalDate.parse(it, formato) },
                                        vigenciaHastaString?.let { LocalDate.parse(it, formato) }
                                    )
                                    lista.add(instancia as T)
                                }

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

    // NO DEBERÍA SER obtenerPromosPorTarjeta ? Sin plural
    suspend fun  obtenerPromosPorTarjetas(tarjeta: String): List<Promocion> = suspendCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com/")
        val promocionRef = database.getReference("/Promocion")
        val lista: MutableList<Promocion> = mutableListOf()
        promocionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children){
                        val listaCampo = data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {})
                        if (listaCampo != null && listaCampo.contains(tarjeta)) {
                            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val vigenciaDesdeString: String? =
                                data.child("vigenciaDesde").getValue(String::class.java)
                            val vigenciaHastaString: String? =
                                data.child("vigenciaHasta").getValue(String::class.java)
                            val instancia =
                                Promocion(data.key,data.child("categoria")
                                    .getValue(String::class.java),
                                    data.child("comercio").getValue(String::class.java),
                                    data.child("dias").getValue(object :
                                        GenericTypeIndicator<List<String?>>() {}),
                                    data.child("tarjetas").getValue(object :
                                        GenericTypeIndicator<List<String?>>() {}),
                                    data.child("proveedor").getValue(String::class.java),
                                    data.child("titulo").getValue(String::class.java),
                                    data.child("tope").getValue(String::class.java),
                                    data.child("tyc").getValue(String::class.java),
                                    data.child("url").getValue(String::class.java),
                                    vigenciaDesdeString?.let {
                                        LocalDate.parse(
                                            it,
                                            formato
                                        ) },
                                    vigenciaHastaString?.let {
                                        LocalDate.parse(
                                            it,
                                            formato
                                        )
                                    }
                                )
                            lista.add(instancia)
                        }

                    }
                }

                continuation.resume(lista)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error","Error en lectura de bd")
                continuation.resumeWithException(databaseError.toException())
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


//leerBdClase():
        var instancia = LecturaBD()

        setContentView(R.layout.activity_main)

//Entidad:
        instancia.leerBdClase<Entidad>("Entidad","tipo","Bancaria"){list ->
            for (item in list) {
                when (item) {
                    is Entidad -> println("Instancia de Entidad: ${item.nombre}")
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

//comercio
        instancia.leerBdClase<Comercio>("Comercio","nombre","Almado"){list ->
            for (item in list) {
                when (item) {
                    is Comercio -> {
                        println("Instancia de Comercio: ${item.nombre}")
                        println("Instancia de Comercio: ${item.logo}")
                        println("Instancia de Comercio: ${item.categoria}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }


//Tarjeta:
        instancia.leerBdClase<Tarjeta>("Tarjeta","procesadora","Visa"){list ->
            for (item in list) {
                when (item) {
                    is Tarjeta -> {
                        println("Instancia de Tarjeta: ${item.entidad}")
                        println("Instancia de Tarjeta: ${item.procesadora}")
                        println("Instancia de Tarjeta: ${item.segmento}")
                        println("Instancia de Tarjeta: ${item.tipoTarjeta}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

//Sucursal
      instancia.leerBdClase<Sucursal>("Sucursal","idComercio","-NbqSvEi9vx2qhpYrZhZ"){list ->
            for (item in list) {
                when (item) {
                    is Sucursal -> {
                        println("Instancia de Sucursal: ${item.direccion}")
                        println("Instancia de Sucursal: ${item.idComercio}")
                        println("Instancia de Sucursal: ${item.latitud}")
                        println("Instancia de Sucursal: ${item.longitud}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

//Promocion

        instancia.leerBdClase<Promocion>("Promocion","categoria","Gastronomía"){list ->
            for (item in list) {
                when (item) {
                    is Promocion -> {
                        println("Instancia de Promocion: ${item.categoria}")
                        println("Instancia de Promocion: ${item.comercio}")
                        println("Instancia de Promocion: ${item.dias}")
                        println("Instancia de Promocion: ${item.tarjetas}")
                        println("Instancia de Promocion: ${item.proveedor}")
                        println("Instancia de Promocion: ${item.titulo}")
                        println("Instancia de Promocion: ${item.tope}")
                        println("Instancia de Promocion: ${item.tyc}")
                        println("Instancia de Promocion: ${item.url}")
                        println("Instancia de Promocion: ${item.vigenciaDesde}")
                        println("Instancia de Promocion: ${item.vigenciaHasta}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

 */

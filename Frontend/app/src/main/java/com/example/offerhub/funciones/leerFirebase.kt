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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    var id: String? = null
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
    val vigenciaDesde: LocalDate?
    val vigenciaHasta: LocalDate?
    val estado: String?
    val logo: String?

    // Constructor primario

    constructor(id:String?,categoria: String?, comercio: String?, cuotas: String?, dias: List<String?>?, porcentaje: String?, proveedor: String?, sucursales: List<String?>?, tarjetas: List<String?>?,
                tipoPromocion: String?, titulo: String?, topeNro: String?, topeTexto: String?, tyc: String?, url: String?, vigenciaDesde: LocalDate?,
                vigenciaHasta: LocalDate?,estado:String?, logo: String?) {
        this.id = id
        this.categoria = categoria
        this.comercio = comercio
        this.cuotas = cuotas
        this.dias = dias
        this.porcentaje = porcentaje
        this.proveedor = proveedor
        this.sucursales = sucursales
        this.tarjetas = tarjetas
        this.tipoPromocion=tipoPromocion
        this.titulo=titulo
        this.topeNro=topeNro
        this.topeTexto=topeTexto
        this.tyc =tyc
        this.url=url
        this.vigenciaDesde = vigenciaDesde
        this.vigenciaHasta = vigenciaHasta
        this.estado = estado
        this.logo = logo
    }

}

class LecturaBD {

    fun leerBdString(tabla: String,campoFiltro: String,valorFiltro: String,campoRetorno: String,callback: (MutableList<String>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
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

    fun <T> traerClasesXFiltro(tabla: String,campoFiltro: String,valorFiltro: String,callback: (MutableList<T>) -> Unit){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
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
                                    val comercio: String? = data.child("comercio").getValue(String::class.java)
                                    val coroutineScope = CoroutineScope(Dispatchers.Main)
                                    var logo: String? = ""

                                    coroutineScope.launch {
                                        try {
                                            if(comercio != null){
                                                logo = Funciones().traerLogoComercio(comercio)}
                                            else{logo = ""}

                                        } catch (e: Exception) {
                                            println("Error al obtener promociones: ${e.message}")
                                        }
                                    }

                                    Log.d("logo", "${ logo }")
                                    val instancia = Promocion(data.key,data.child("categoria").getValue(String::class.java),  data.child("comercio").getValue(String::class.java),
                                        data.child("cuotas").getValue(String::class.java),
                                        data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("porcentaje").getValue(String::class.java), data.child("proveedor").getValue(String::class.java),
                                        data.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("tipoPromocion").getValue(String::class.java),
                                        data.child("titulo").getValue(String::class.java), data.child("topeNro").getValue(String::class.java),
                                        data.child("topeTexto").getValue(String::class.java),data.child("tyc").getValue(String::class.java),
                                        data.child("url").getValue(String::class.java),vigenciaDesdeString?.let { LocalDate.parse(it, formato) },
                                        vigenciaHastaString?.let { LocalDate.parse(it, formato)},
                                        data.child("tipoPromocion").getValue(String::class.java), logo
                                    )
                                    lista.add(instancia as T)
                                } "Usuario" ->{
                                val instancia = data.child("correo").getValue(String::class.java)?.let {
                                    data.key?.let { it1 ->
                                        Usuario(
                                            it1,
                                            data.child("nombre").getValue(String::class.java)!!,  data.child("correo").getValue(String::class.java),
                                            data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                            data.child("favoritos").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                            data.child("wishlistComercio").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                            data.child("wishlistRubro").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                            data.child("promocionesReintegro").getValue(object : GenericTypeIndicator<List<String?>>() {})
                                        )
                                    }
                                }


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

    inline fun <reified T> traerClasesXFiltros(tabla: String, filtros: List<Pair<String, String>>, crossinline callback: (MutableList<T>) -> Unit ) {
        var lista: MutableList<T>? = null

        traerClasesXFiltro<T>(tabla, filtros[0].first, filtros[0].second) { list ->
            lista = list
            var i = 1
            val atributosListas = listOf("dias", "sucursales", "tarjetas")
            while (i < filtros.size) {
                val campoFiltro = filtros[i].first
                val valorFiltro = filtros[i].second

                lista = lista?.filter { clase ->
                    try {
                        val field = (T::class.java).getDeclaredField(campoFiltro)
                        field.isAccessible = true
                        val atributoClase = field.get(clase)

                        val booleano = if (campoFiltro in atributosListas) {
                            if (atributoClase is List<*>) {
                                valorFiltro in atributoClase
                            } else {
                                false
                            }
                        } else {
                            atributoClase == valorFiltro
                        }

                        booleano
                    } catch (e: NoSuchFieldException) {
                        false
                    }
                }?.toMutableList()


                i++
            }
            lista?.let { callback(it) }
        }

    }

    suspend fun filtrarPromos(filtros: List<Pair<String, String>>, usuario: Usuario): List<Promocion> {
        var instancia = Funciones()
        var promos = instancia.obtenerPromociones(usuario)
        var i = 0
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (i < filtros.size) {
            val campoFiltro = filtros[i].first
            val valorFiltro = filtros[i].second

            when(campoFiltro) {
                    "categoria" -> {
                        promos = promos.filter { promo -> promo.categoria == valorFiltro }.toMutableList()

                    } "comercio" -> {
                        promos = promos.filter { promo -> promo.comercio == valorFiltro }.toMutableList()

                    } "dias" -> {
                        promos = promos.filter { promo -> promo.dias!!.contains(valorFiltro)  }.toMutableList()

                    } "porcentaje" -> {
                        promos = promos.filter { promo -> promo.porcentaje == valorFiltro }.toMutableList()

                    } "proveedor" -> {
                        promos = promos.filter { promo -> promo.proveedor == valorFiltro }.toMutableList()

                    } "tarjetas" -> {
                        promos = promos.filter { promo -> promo.tarjetas!!.contains(valorFiltro)  }.toMutableList()

                    } "tipoPromocion" -> {
                        promos = promos.filter { promo -> promo.tipoPromocion == valorFiltro }.toMutableList()

                    } "topeNro" -> {
                        promos = promos.filter { promo -> promo.topeNro == valorFiltro }.toMutableList()

                    } "topeTexto" -> {
                        promos = promos.filter { promo -> promo.topeTexto == valorFiltro }.toMutableList()

                    } "vigenciaDesde" -> {
                        val fechaLocal = LocalDate.parse(valorFiltro, formato)
                        promos = promos.filter { promo -> promo.vigenciaDesde == fechaLocal }.toMutableList()

                    } "vigenciaHasta" -> {
                        val fechaLocal = LocalDate.parse(valorFiltro, formato)
                        promos = promos.filter { promo -> promo.vigenciaHasta == fechaLocal }.toMutableList()

                    }
                    else -> throw IllegalArgumentException("Atributo desconocido")
                }
            i += 1
            }

        return promos
    }


    suspend fun  obtenerPromosPorTarjeta(tarjeta: String): List<Promocion> = suspendCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
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
                            val vigenciaDesdeString: String? = data.child("vigenciaDesde").getValue(String::class.java)
                            val vigenciaHastaString: String? = data.child("vigenciaHasta").getValue(String::class.java)
                            val comercio: String? = data.child("comercio").getValue(String::class.java)
                            val coroutineScope = CoroutineScope(Dispatchers.Main)
                            var logo: String? = ""

                            coroutineScope.launch {
                                try {
                                    if(comercio != null){
                                    logo = Funciones().traerLogoComercio(comercio)}
                                    else{logo = ""}
                                } catch (e: Exception) {
                                    println("Error al obtener promos: ${e.message}")
                                }
                            }


                            val instancia = Promocion(data.key,data.child("categoria").getValue(String::class.java),  comercio,
                                data.child("cuotas").getValue(String::class.java),
                                data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("porcentaje").getValue(String::class.java), data.child("proveedor").getValue(String::class.java),
                                data.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tipoPromocion").getValue(String::class.java),
                                data.child("titulo").getValue(String::class.java), data.child("topeNro").getValue(String::class.java),
                                data.child("topeTexto").getValue(String::class.java),data.child("tyc").getValue(String::class.java),
                                data.child("url").getValue(String::class.java),vigenciaDesdeString?.let { LocalDate.parse(it, formato) },
                                vigenciaHastaString?.let { LocalDate.parse(it, formato) },
                                data.child("estado").getValue(String::class.java), logo)
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

    suspend fun  obtenerListaOrdenadaSegun(promocionesDesordenadas: List<Promocion>, atributo: String, valorAtributo: String): List<Promocion> = suspendCoroutine { continuation ->
        val listaOrdenada = promocionesDesordenadas
            .filter { promocion ->
                val atributoValor = promocion.javaClass.getDeclaredField(atributo).apply { isAccessible = true }
                val valor = atributoValor.get(promocion).toString()
                valor == valorAtributo
            }
            .sortedBy { it.javaClass.getDeclaredField(atributo).get(it).toString() }

        continuation.resume(listaOrdenada)
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


//traerClasesXFiltro():
        var instancia = LecturaBD()

        setContentView(R.layout.activity_main)

//Entidad:
        instancia.traerClasesXFiltro<Entidad>("Entidad","tipo","Bancaria"){list ->
            for (item in list) {
                when (item) {
                    is Entidad -> println("Instancia de Entidad: ${item.nombre}")
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

//comercio
        instancia.traerClasesXFiltro<Comercio>("Comercio","nombre","Almado"){list ->
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
        instancia.traerClasesXFiltro<Tarjeta>("Tarjeta","procesadora","Visa"){list ->
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
      instancia.traerClasesXFiltro<Sucursal>("Sucursal","idComercio","-NbqSvEi9vx2qhpYrZhZ"){list ->
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

        instancia.traerClasesXFiltro<Promocion>("Promocion","categoria","Gastronomía"){list ->
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

 //Usuario
        instancia.traerClasesXFiltro<Usuario>("Usuario","contrasenia","carlitos"){ list ->
            for (item in list) {
                when (item) {
                    is Usuario -> {
                        Log.d("Instancia de Sucursal", "${item.id}")
                        Log.d("Instancia de Sucursal", "${item.correo}")
                        Log.d("Instancia de Sucursal", "${item.nombre}")
                        Log.d("Instancia de Sucursal", "${item.contrasenia}")
                        Log.d("Instancia de Sucursal", "${item.favoritos}")
                        Log.d("Instancia de Sucursal", "${item.tarjetas}")

                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

// MUCHOS FILTROS

// PROMOCION

        var instancia = LecturaBD()

        setContentView(R.layout.activity_main)

        val filtros = listOf(
            "categoria" to "Educación",
            "tipoPromocion" to "Cuotas"
        )


        instancia.traerClasesXFiltros<Promocion>("Promocion",filtros){list ->
            Log.d("TAMAÑO LISTA", "TAMAÑO: $list.size")
            for (item in list) {
                when (item) {
                    is Promocion -> {
                        Log.d("Promocion", "titulo: ${item.titulo}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }

   // CON LISTAS

        var instancia = LecturaBD()

        setContentView(R.layout.activity_main)

        val filtros = listOf(
            "categoria" to "Educación",
            "tarjetas" to "-NcDHYyW9d0QE9gyP8Nt",
            "dias" to "Martes"
        )


        instancia.traerClasesXFiltros<Promocion>("Promocion",filtros){list ->
            for (item in list) {
                when (item) {
                    is Promocion -> {
                        Log.d("Promocion", "titulo: ${item.titulo}")
                    }
                    else -> throw IllegalArgumentException("Tipo de clase desconocido")
                }
            }
        }
 FILTRAR PROMOS

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var instancia = LecturaBD()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        var usuario = Usuario("FDKPMRrqo1OnpxZQUVAYmHbywjw2","Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)
        val filtros = listOf(
            "categoria" to "Educación",
        )

        Log.d("promo", "hola?")
        coroutineScope.launch {
            try {
                var promos = instancia.filtrarPromos(filtros, usuario)
                for(promo in promos){
                    Log.d("promo", "${ promo.categoria }")
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

OBTENER PROMOS POR TARJETA

val coroutineScope = CoroutineScope(Dispatchers.Main)
        val instancia = LecturaBD()
        coroutineScope.launch {
            try {
                val logo = instancia.obtenerPromosPorTarjeta("-Ne52v1WN0OfZwqqBx_H")
                if (logo != null) {
                    Log.d("logo", "${ logo.size}")
                }

            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

 */

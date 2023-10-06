package com.example.offerhub
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Parcelable
import com.google.firebase.database.ValueEventListener
import org.threeten.bp.format.DateTimeFormatter
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import com.example.offerhub.funciones.formatearFecha
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.text.Normalizer
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
    var cuil: String?

    // Constructor primario
    constructor(id:String?,nombre: String?, categoria: String?,logo:String?,cuil:String?) {
        this.id = id
        this.nombre = nombre
        this.categoria = categoria
        this.logo =  logo
        this.cuil = cuil
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
@Parcelize
class Sucursal(
    // Propiedades (atributos) de la clase
    var id: String?,
    var direccion: String?,
    var idComercio: String?,
    var latitud: Double?,
    var longitud: Double?
    ): Parcelable

@Parcelize
class Promocion(
    var id: String? = null,
    var categoria: String? = null,
    var comercio: String? = null,
    var cuotas: String? = null,
    val dias: List<String?>? = null,
    var porcentaje: String? = null,
    var proveedor: String? = null,
    val idSucursales: List<String?>? = null,
    var sucursales: List<Sucursal?>? = null,
    val tarjetas: List<String?>? = null,
    val tipoPromocion: String? = null,
    val titulo: String? = null,
    val topeNro: String? = null,
    val topeTexto: String? = null,
    val tyc: String? = null,
    val descripcion: String? = null,
    val url: String? = null,
    val vigenciaDesde: LocalDate? = null,
    val vigenciaHasta: LocalDate? = null,
    val estado: String? = null,
) : Parcelable {
    fun obtenerTextoVigencia(): String? {
        val vigenciaDesde = this.vigenciaDesde
        val vigenciaHasta = this.vigenciaHasta

        val textoVigencia = when {
            vigenciaDesde != null && vigenciaHasta != null -> {
                return "Desde ${formatearFecha(vigenciaDesde)} hasta ${formatearFecha(vigenciaHasta)}"
            }
            vigenciaDesde != null -> {
                return "Desde ${formatearFecha(vigenciaDesde)}"
            }
            vigenciaHasta != null -> {
                return "Hasta ${formatearFecha(vigenciaHasta)}"
            }
            else -> {
                return ""
            }
        }
    }
    fun obtenerDesc(): kotlin.String {
        if(this.tipoPromocion=="Reintegro" || this.tipoPromocion=="Descuento"){
            return this.porcentaje.toString()
        } else if (this.tipoPromocion=="2x1") {
            return "2x1"
        } else if (this.tipoPromocion=="Cuotas"){
            return this.cuotas.toString()
        }
        return ""
    }

    suspend fun obtenerSucursales() {
        val listaSucursales: MutableList<Sucursal> = mutableListOf()
        val instancia = LeerId()

        this.idSucursales?.forEach { idSucursal ->
            idSucursal?.let { id ->
                val sucursal = instancia.obtenerSucursalPorId(id)
                sucursal?.let {
                    listaSucursales.add(it)
                }
            }
        }
        Log.d("DB - Obtener Sucursales", "Obteniendo sucursales de DB")
        this.sucursales = listaSucursales
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
                Log.d("DB - Error","Error en lectura de bd")
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
                                    val instancia = Comercio(data.key,data.child("nombre").getValue(String::class.java),  data.child("categoria").getValue(String::class.java),data.child("logo").getValue(String::class.java),data.child("cuil").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                                "Tarjeta" ->{
                                    val instancia = Tarjeta(data.key,data.child("procesadora").getValue(String::class.java),  data.child("segmento").getValue(String::class.java),data.child("tipoTarjeta").getValue(String::class.java),data.child("entidad").getValue(String::class.java))
                                    lista.add(instancia as T)
                                }
                                "Sucursal" ->{
                                    var latitud = data.child("latitud").getValue(String::class.java)
                                    if (latitud != null) {
                                        if(latitud.contains("posee") || latitud.contains("Error")){
                                            latitud = "0"
                                        }
                                    }
                                    var longitud = data.child("longitud").getValue(String::class.java)
                                    if (longitud != null) {
                                        if(longitud.contains("posee") || longitud.contains("Error")){
                                            longitud = "0"
                                        }
                                    }
                                    val instancia = Sucursal(data.key,data.child("direccion").getValue(String::class.java),  data.child("idComercio").getValue(String::class.java),
                                        latitud?.toDouble(),
                                        longitud?.toDouble()
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
                                    var desde: LocalDate?
                                    var hasta: LocalDate?
                                    if(data.child("vigenciaDesde").getValue(String::class.java) != "No posee"){ desde = vigenciaDesdeString?.let { LocalDate.parse(it, formato) }} else {desde = null}
                                    if(data.child("vigenciaHasta").getValue(String::class.java) != "No posee"){hasta = vigenciaHastaString?.let { LocalDate.parse(it, formato)}} else {hasta = null}
                                    val instancia = Promocion(
                                        data.key,
                                        data.child("categoria").getValue(String::class.java),
                                        data.child("comercio").getValue(String::class.java),
                                        data.child("cuotas").getValue(String::class.java),
                                        data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("porcentaje").getValue(String::class.java),
                                        data.child("proveedor").getValue(String::class.java),
                                        data.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        mutableListOf(),
                                        data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                        data.child("tipoPromocion").getValue(String::class.java),
                                        data.child("titulo").getValue(String::class.java),
                                        data.child("topeNro").getValue(String::class.java),
                                        data.child("topeTexto").getValue(String::class.java),
                                        data.child("tyc").getValue(String::class.java),
                                        data.child("descripcion").getValue(String::class.java),
                                        data.child("url").getValue(String::class.java),
                                        desde,
                                        hasta,
                                        data.child("tipoPromocion").getValue(String::class.java)

                                    )
                                    lista.add(instancia as T)
                                } "Usuario" ->{
                                val instancia = data.child("correo").getValue(String::class.java)?.let {
                                    data.key?.let { it1 ->
                                        Usuario(
                                            it1,
                                            data.child("nombre").getValue(String::class.java)!!,  data.child("correo").getValue(String::class.java),
                                            data.child("tarjetas").getValue(object : GenericTypeIndicator<MutableList<String?>>() {}),
                                            data.child("favoritos").getValue(object : GenericTypeIndicator<MutableList<String?>>() {}),
                                            data.child("wishlistComercio").getValue(object : GenericTypeIndicator<MutableList<String?>>() {}),
                                            data.child("wishlistRubro").getValue(object : GenericTypeIndicator<MutableList<String?>>() {}),
                                            data.child("promocionesReintegro").getValue(object : GenericTypeIndicator<MutableList<String?>>() {}),
                                                    data.child("homeModoFull").getValue(String::class.java)
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

    fun String.removeAccents(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("[^\\p{ASCII}]").replace(normalized, "")
    }

    suspend fun filtrarPromos(filtros: List<Pair<String, String>>, usarAnd: Boolean=true): List<Promocion> {
        var instancia = Funciones()
        var promos = UserViewModelSingleton.getUserViewModel().listadoDePromosDisp
        var promosAux : MutableList<Promocion> = mutableListOf()
        var i = 0
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (i < filtros.size) {
            val campoFiltro = filtros[i].first
            val valorFiltro = filtros[i].second

            val filtro: (Promocion) -> Boolean = when (campoFiltro) {
                "titulo" -> { promo ->
                    val titulo = promo.titulo
                    titulo != null && titulo.removeAccents()
                        .lowercase()
                        .contains(valorFiltro.removeAccents().lowercase()) }
                "categoria" -> { promo ->
                    val categoria = promo.categoria
                    categoria != null && categoria.removeAccents()
                        .lowercase()
                        .contains(valorFiltro.removeAccents().lowercase()) }
                "dias" -> { promo -> promo.dias!!.contains(valorFiltro) }
                "porcentaje" -> { promo -> promo.porcentaje == valorFiltro }
                "proveedor" -> { promo -> promo.proveedor == valorFiltro }
                "tarjetas" -> { promo -> promo.tarjetas!!.contains(valorFiltro) }
                "tipoPromocion" -> { promo -> promo.tipoPromocion == valorFiltro }
                "topeNro" -> { promo -> promo.topeNro == valorFiltro }
                "topeTexto" -> { promo -> promo.topeTexto == valorFiltro }
                "vigenciaDesde" -> {promo -> promo.vigenciaDesde == LocalDate.parse(valorFiltro, formato) }
                "vigenciaHasta" -> { promo -> promo.vigenciaHasta == LocalDate.parse(valorFiltro, formato) }
                else -> throw IllegalArgumentException("Atributo desconocido")
            }

            if (usarAnd) {
                // Filtrar con operador AND (todos los filtros deben coincidir)
                promosAux = promos.filter { promo -> filtro(promo) }.toMutableList()
            } else {
                // Filtrar con operador OR (al menos un filtro debe coincidir)
                val promosFiltradas = promos.filter { promo -> filtro(promo) }.toMutableList()
                promosAux.addAll(promosFiltradas)
            }

            i++
        }

        return promosAux.distinct()
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
                            val desdeFormateado: LocalDate?
                            val hastaFormateado: LocalDate?
                            var vigenciaDesdeString = data.child("vigenciaDesde").getValue(String::class.java)
                            if(vigenciaDesdeString != "No posee"){
                                desdeFormateado = LocalDate.parse(vigenciaDesdeString, formato)
                            } else { desdeFormateado = null }
                            var vigenciaHastaString: String? = data.child("vigenciaHasta").getValue(String::class.java)
                            if(vigenciaHastaString != "No posee"){
                                hastaFormateado = LocalDate.parse(vigenciaHastaString, formato)
                            } else { hastaFormateado = null }
                            val comercio: String? = data.child("comercio").getValue(String::class.java)
                            val coroutineScope = CoroutineScope(Dispatchers.Main)
                            val instancia = Promocion(data.key,data.child("categoria").getValue(String::class.java),  comercio,
                                data.child("cuotas").getValue(String::class.java),
                                data.child("dias").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("porcentaje").getValue(String::class.java), data.child("proveedor").getValue(String::class.java),
                                data.child("sucursales").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                mutableListOf(),
                                data.child("tarjetas").getValue(object : GenericTypeIndicator<List<String?>>() {}),
                                data.child("tipoPromocion").getValue(String::class.java),
                                data.child("titulo").getValue(String::class.java), data.child("topeNro").getValue(String::class.java),
                                data.child("topeTexto").getValue(String::class.java),data.child("tyc").getValue(String::class.java),data.child("descripcion").getValue(String::class.java),
                                data.child("url").getValue(String::class.java),
                                desdeFormateado,
                                hastaFormateado,
                                data.child("estdeado").getValue(String::class.java))
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

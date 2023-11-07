package com.example.offerhub

import android.content.Context
import android.util.Log
import com.example.offerhub.data.Categoria
import com.example.offerhub.data.UserPartner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class Funciones {
    val instancialeerId = LeerId()
    val instanciaLectura = LecturaBD()
    val instanciaEscritura = EscribirBD()
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    suspend fun traerEntidades(): List<Entidad> = coroutineScope {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Entidad").get().await()
        var entidades: MutableList<Entidad> = mutableListOf()

        for (snapshot in dataSnapshot.children) {
            var id = snapshot.key
            var nombre = snapshot.child("nombre").getValue(String::class.java)
            var tipo = snapshot.child("tipo").getValue(String::class.java)
            var entidad = Entidad(id,nombre, tipo)
            entidades.add(entidad)
        }

        entidades
    }

    suspend fun obtenerTarjetasDisponibles(): MutableList<Tarjeta> = coroutineScope {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Tarjeta").get().await()
        var tarjetas: MutableList<Tarjeta> = mutableListOf()

        for(snapshot in dataSnapshot.children) {
            var id = snapshot.key
            var entidad = snapshot.child("entidad").getValue(String::class.java)
            var procesadora = snapshot.child("procesadora").getValue(String::class.java)
            var segmento = snapshot.child("segmento").getValue(String::class.java)
            var tipoTarjeta = snapshot.child("tipoTarjeta").getValue(String::class.java)
            var tarjeta = Tarjeta(id, procesadora, segmento, tipoTarjeta, entidad)
            tarjetas.add(tarjeta)

        }

        tarjetas
    }


    //Obtiene las promociones de comercios que aplican a cualq usuario, sin necesidad de tarjetas.
    suspend fun obtenerPromocionesComunes(): List<Promocion> = coroutineScope {
        val listaPromos: MutableList<Promocion> = mutableListOf()
        val promosDeferred = async {
            val promos = instanciaLectura.obtenerPromosPorTarjeta(null)
            for (promo in promos){
                if((promo.estado!=null) && (promo.estado.lowercase() == "aprobado")){
                    listaPromos.add(promo)
                }
            }
        }
        promosDeferred.await() // Esperar a que se completen las promociones

        listaPromos
    }

    suspend fun obtenerPromociones(usuario: Usuario?): MutableList<Promocion> = coroutineScope {
        val listaPromos: MutableList<Promocion> = mutableListOf()
        var i = 0
        val deferredPromos = coroutineScope.async {
            val promosComunes = obtenerPromocionesComunes()
            if (promosComunes != null) {
                listaPromos.addAll(promosComunes)
            }

        usuario?.tarjetas?.map { tarjeta ->
            val promos = tarjeta?.let { instanciaLectura.obtenerPromosPorTarjeta(it) }
            if (promos != null) {
                listaPromos.addAll(promos)
            }
        }
        }
        Log.d("DB - Obteniendo Promociones","Se estan descargando promociones de la DB")
        deferredPromos?.await()
        listaPromos
    }


    suspend fun obtenerTarjeta(entidad: String, procesadora: String, segmento: String, tipo:String):Tarjeta? {
        var instancia = LeerId()
        var interfaz = InterfaceSinc()
        var tarjetaFinal: Tarjeta? = null
        val resultado = instancia.obtenerIdSinc("Entidad", "nombre", entidad)
        if (resultado != null) {
            val listaEntidades: MutableList<Tarjeta> = interfaz.leerBdClaseSinc<Tarjeta>("Tarjeta","entidad",resultado)
            for (tarjeta in listaEntidades){
                if(tarjeta.procesadora==procesadora && tarjeta.segmento == segmento && tarjeta.tipoTarjeta == tipo){
                    tarjetaFinal = tarjeta
                }
            }
        }
        return tarjetaFinal
    }

    fun agregarPromocionAFavoritos(userId: String, elementoId: String) {
        instanciaEscritura.agregarElementoAListas(userId, elementoId, "Usuario", "favoritos")
    }

    fun elimiarPromocionDeFavoritos(userId: String, elementoId: String){
        instanciaEscritura.eliminarElementoDeListas(userId, elementoId, "Usuario", "favoritos")
    }

    fun agregarPromocionAReintegro(userId: String, elementoId: String) {
        instanciaEscritura.agregarElementoAListas(userId, elementoId, "Usuario", "promocionesReintegro")
    }

    fun elimiarPromocionDeReintegro(userId: String, elementoId: String){
        instanciaEscritura.eliminarElementoDeListas(userId, elementoId, "Usuario", "promocionesReintegro")
    }

    // Por parámetro se pasa el id del usuario y el elemento a agregar a la wishlist (Id del comercio o nombre del rubro)
    fun agregarAWishlist(userId: String, elemento: String) {
        if (elemento in listOf<String>("Gastronomía","Vehículos","Salud y Bienestar","Hogar","Viajes y Turismo","Entretenimiento","Indumentaria","Supermercados","Electrónica","Educación","Niños","Regalos","Bebidas","Librerías","Joyería","Mascotas","Servicios","Otros")) {
            instanciaEscritura.agregarElementoAListas(userId, elemento, "Usuario", "wishlistRubro")
        } else {
            instanciaEscritura.agregarElementoAListas(userId, elemento, "Usuario", "wishlistComercio")
        }

    }

    fun eliminarDeWishlist(userId: String, elemento: String){
        if (elemento in listOf<String>("Gastronomía","Vehículos","Salud y Bienestar","Hogar","Viajes y Turismo","Entretenimiento","Indumentaria","Supermercados","Electrónica","Educación","Niños","Regalos","Bebidas","Librerías","Joyería","Mascotas","Servicios","Otros")) {
            instanciaEscritura.eliminarElementoDeListas(userId, elemento, "Usuario", "wishlistRubro")
        } else {
            instanciaEscritura.eliminarElementoDeListas(userId, elemento, "Usuario", "wishlistComercio")
        }
    }

    fun agregarTarjetasAUsuario(userId: String, tarjetasId: MutableList<String>) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference("Usuario").child(userId).child("tarjetas")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lista = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val valor = snapshot.getValue(String::class.java)
                    if (valor != null) {
                        lista.add(valor)
                    }
                }

                for(id in tarjetasId){
                    val nuevoIndice = lista.size.toString()
                    referencia.child(nuevoIndice).setValue(id)
                        .addOnCompleteListener {}
                    lista.add("")
                }}

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    fun elimiarTarjetaDeUsuario(userId: String, tarjetaId: String){
        instanciaEscritura.eliminarElementoDeListas(userId, tarjetaId, "Usuario", "tarjetas")
    }

    fun eliminarTodasLasTarjetasDeUsuario(userId: String) {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference("Usuario").child(userId).child("tarjetas")

        referencia.removeValue()
            .addOnSuccessListener {
                println("Todas las tarjetas han sido eliminadas con éxito.")
            }
            .addOnFailureListener { error ->
                println("Error al eliminar todas las tarjetas: $error")
            }
    }


    fun editarPerfil(userId: String, atributo: String, valorNuevo: String){
        instanciaEscritura.editarAtributoDeClase("Usuario",userId, atributo,valorNuevo)
    }


    suspend fun obtenerPromocionesFavoritas(usuario: Usuario,promosDisp:MutableList<Promocion> = mutableListOf()): MutableList<Promocion> = coroutineScope {
        val promocionesTotales = if(promosDisp.size>0) promosDisp else obtenerPromociones(usuario)
        val promociones : MutableList<Promocion> = mutableListOf()
        val instancia = LeerId()
        if(usuario.favoritos != null) {
            for (id in usuario.favoritos!!) {
                if (id != null) {
                    var promo=promocionesTotales.find { it -> it.id==id }
                    if(promo!=null){
                        promociones.add(promo)
                    }
                    //instancia.obtenerPromocionPorId(id)?.let { promociones.add(it) }
                }
            }
        }

        promociones
    }

    suspend fun existePromocionEnFavoritos(usuario: Usuario, elementoId: String?): Boolean = coroutineScope {
        usuario.favoritos?.contains(elementoId) == true
    }

    suspend fun existePromocionEnReintegros(usuario: Usuario, elementoId: String?): Boolean = coroutineScope {
        usuario.promocionesReintegro?.contains(elementoId) == true
    }

    suspend fun obtenerPromocionesReintegro(usuario: Usuario,promosDisp:MutableList<Promocion> = mutableListOf()): MutableList<Promocion> = coroutineScope {
        val promocionesTotales = if(promosDisp.size>0) promosDisp else obtenerPromociones(usuario)
        val promociones : MutableList<Promocion> = mutableListOf()

        for(promocion in promocionesTotales){
            if(usuario.promocionesReintegro?.contains(promocion.id) == true){ // el == true lo puso solo por el ?, sino no anda
                promociones.add(promocion)
            }
        }

        promociones
    }

   /* suspend fun buscarPromocionesPorRubro(usuario: Usuario, nombreRubro: String): List<Promocion> = coroutineScope {
        val filtros = listOf(
            "categoria" to nombreRubro,
        )
        val promocionesFiltradas = instanciaLectura.filtrarPromos(filtros)

        promocionesFiltradas
    }

    suspend fun buscarPromocionesPorComercio(usuario: Usuario, idComercio: String): List<Promocion> = coroutineScope {
        val filtros = listOf(
            "comercio" to idComercio,
        )
        val promocionesFiltradas = instanciaLectura.filtrarPromos(filtros)

        promocionesFiltradas
    }*/

    suspend fun traerUsuarioActual(): Usuario? = coroutineScope {

        val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
        val currentUser = auth.currentUser
        var usuario : Usuario?

        if (currentUser != null) {
            Log.d("DB - IDCurrentUser", "${currentUser.uid}")
        }

        if (currentUser != null) {
            usuario =  instancialeerId.obtenerUsuarioPorId(currentUser.uid)
            if (usuario != null) {
                Log.d("DB - ID", "${usuario.nombre}")
            }
        } else {
            usuario =  null
            Log.d("DB - ID", "Usuario no autenticado") // Manejar el caso en que el usuario no esté autenticado
        }

        usuario
    }

    suspend fun traerUsuarioPartner(): UserPartner? = coroutineScope {

        val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
        val currentUser = auth.currentUser
        var usuario : UserPartner?

        if (currentUser != null) {
            Log.d("DB - IDCurrentUser", "${currentUser.uid}")
        }

        if (currentUser != null) {
            usuario =  instancialeerId.obtenerUsuarioPartnerPorId(currentUser.uid)
            if (usuario != null) {
                Log.d("DB - ID", "${usuario.nombreDeEmpresa}")
            }
        } else {
            usuario =  null
            Log.d("DB - ID", "Usuario no autenticado") // Manejar el caso en que el usuario no esté autenticado
        }

        usuario
    }

    suspend fun traerUsuarioPartnerActual(): UserPartner? = coroutineScope {

        val auth: FirebaseAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
        val currentUser = auth.currentUser
        var usuario : UserPartner?

        if (currentUser != null) {
            Log.d("DB - IDCurrentUser", "${currentUser.uid}")
        }

        if (currentUser != null) {
            usuario =  instancialeerId.obtenerUsuarioPartnerPorId(currentUser.uid)
            if (usuario != null) {
                Log.d("DB - CUIL", "${usuario.cuil}")
            }
        } else {
            usuario =  null
            Log.d("DB - ID", "Usuario no autenticado") // Manejar el caso en que el usuario no esté autenticado
        }

        usuario
    }

    fun tarjetasComunes(usuario:Usuario?,promocion:Promocion):List<String>{
        val lista = mutableListOf<String>()
        if (usuario != null) {
            for (tarj in usuario.tarjetas!!){
                if(promocion.tarjetas?.contains(tarj) == true){
                    if (tarj != null) {
                        lista.add(tarj)
                    }
                }
            }
        }
        return lista
    }

    suspend fun traerInfoComercio(idComercio: String?,attr:String): String? = coroutineScope {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Comercio").child(idComercio.toString()).get().await()
        var value: String? = ""

        if (dataSnapshot.exists()) {
            value = dataSnapshot.child(attr).getValue(String::class.java)
        }
        value
    }

    suspend fun traerLogoComercio(idComercio: String?): String? = coroutineScope {
        traerInfoComercio(idComercio,"logo")
    }
    suspend fun traerNombeEntidad(idEntidad: String?): String? = coroutineScope {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("Entidad").child(idEntidad.toString()).get().await()
        var nombre: String? = ""

        if (dataSnapshot.exists()) {
            nombre = dataSnapshot.child("nombre").getValue(String::class.java)
        }

        nombre
    }

    fun obtenerCategorias(contexto: Context) : List<Categoria>{
        return listOf(
            Categoria(
                contexto,
                "Gastronomía",
                "cat_gastronomia"
            ),
            Categoria(contexto, "Vehículos", "cat_vehiculos"),
            Categoria(
                contexto,
                "Salud y Bienestar",
                "cat_salud_y_bienestar"
            ),
            Categoria(contexto, "Hogar", "cat_hogar"),
            Categoria(
                contexto,
                "Viajes y Turismo",
                "cat_viajes"
            ),
            Categoria(
                contexto,
                "Entretenimiento",
                "cat_entretenimiento"
            ),
            Categoria(
                contexto,
                "Indumentaria",
                "cat_indumentaria"
            ),
            Categoria(
                contexto,
                "Supermercados",
                "cat_supermercados"
            ),
            Categoria(
                contexto,
                "Electrónica",
                "cat_electronica"
            ),
            Categoria(contexto, "Educación", "cat_educacion"),
            Categoria(contexto, "Niños", "cat_ninos"),
            Categoria(contexto, "Regalos", "cat_regalos"),
            Categoria(contexto, "Bebidas", "cat_bebidas"),
            Categoria(contexto, "Joyería", "cat_joyeria"),
            Categoria(contexto, "Librerías", "cat_librerias"),
            Categoria(contexto, "Mascotas", "cat_mascotas"),
            Categoria(contexto, "Servicios", "cat_servicios"),
            Categoria(contexto, "Otros", "cat_otros")
        )
    }


    fun elimiarUsuario(userId: String){
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
        val referencia = database.getReference("Usuario")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var valor = snapshot.key
                    if (valor == userId) {
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

    suspend fun traerIdsPartners(): List<String> = coroutineScope {
        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com").reference
        val dataSnapshot = database.child("UsuarioPartner").get().await()
        var ids: MutableList<String> = mutableListOf()

        for (snapshot in dataSnapshot.children) {
            if(snapshot.key != null) {
                ids.add(snapshot.key!!)
            }
        }

        ids
    }


}

//ejemplos llamados
/*
OBTENER PROMOCIONES:
        var instancia = Funciones()
        var tarjetas: List<String?> = listOf("-Ne5dQiaSXSCnMJmukwT")
        var usuario1 = Usuario("aflkaslfa","Pepe", "usuario1@example.com", tarjetas,null,null,null,null)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            try {
                var promociones = instancia.obtenerPromociones(usuario1)
                println("Promociones obtenidas:")
                for (promo in promociones){
                    promo.titulo?.let { Log.d("promo", it) }
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: {e.message}")
            }
        }

 Obtener tarjeta:
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                val resultado = instancia.obtenerTarjeta("Banco Galicia", "Visa", "No posee","Débito")
                if (resultado != null) {
                    resultado.entidad?.let { Log.d("Resultado", it) }
                    resultado.id?.let { Log.d("Resultado", it) }
                }
                else{
                   Log.d("Resultado","NULO")
                }
            } catch (e: Exception) {
                Log.d("Resultado","ERROR")
            }
        }

  ObtenerPromocionesComunes:
          val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            try {
                var promociones = instancia.obtenerPromocionesComunes()
                println("Promociones obtenidas:")
                for (promo in promociones){
                    promo.titulo?.let { Log.d("promo", it) }
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }


AGREGAR FAVORITOS

        var instancia = Funciones()
        instancia.agregarPromocionAFavoritos("-NdfTbz8V6THp1xIC37f","-NcDHhG4OLbring2tKyp")

ELIMINAR FAVORITOS

        var instancia = Funciones()
        instancia.elimiarPromocionDeFavoritos("-NdfTbz8V6THp1xIC37f","-NcDHhG4OLbring2tKyp")

AGREGAR REINTEGRO

        var instancia = Funciones()
        instancia.agregarPromocionAReintegro("-NdfTbz8V6THp1xIC37f","-NcDHhG4OLbring2tKyp")

ELIMINAR REINTEGRO

        var instancia = Funciones()
        instancia.elimiarPromocionDeReintegro("-NdfTbz8V6THp1xIC37f","-NcDHhG4OLbring2tKyp")

AGREGAR COMERCIO A WISHLIST

        var instancia = Funciones()
        instancia.agregarAWishlist("-NdfTbz8V6THp1xIC37f","-NcDPGdF2TegzU2mg32k")

ELIMINAR COMERCIO DE WISHLIST

        var instancia = Funciones()
        instancia.eliminarDeWishlist("-NdfTbz8V6THp1xIC37f","-NcDPGdF2TegzU2mg32k")

AGREGAR RUBRO A WISHLIST

        var instancia = Funciones()
        instancia.agregarAWishlist("-NdfTbz8V6THp1xIC37f","Joyería")

ELIMINAR RUBRO DE WISHLIST

        var instancia = Funciones()
        instancia.eliminarDeWishlist("-NdfTbz8V6THp1xIC37f","Joyería")

AGREGAR TARJETA A USUARIO

        var instancia = Funciones()
        instancia.agregarTarjetaAUsuario("-Ndg5uxYvmAkEIpthvNQ", "-NcDI2VVY0uVeOf4jzEI")

ELIMINAR TARJETA DE USUARIO

        var instancia = Funciones()
        instancia.elimiarTarjetaDeUsuario("-Ndg5uxYvmAkEIpthvNQ", "-NcDI2VVY0uVeOf4jzEI")

EDITAR PERFIL

        var instancia = Funciones()
        instancia.editarPerfil("-Ndg5uxYvmAkEIpthvNQ", "contrasenia", "goleador")

OBTENER PROMOCIONES FAVORITOS

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var instancia = Funciones()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        var usuario = Usuario("1","Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)


        coroutineScope.launch {
            try {
                var lista = instancia.obtenerPromocionesFavoritas(usuario)
                for(promo in lista) {
                    Log.d("promo", "${ promo.titulo }")
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

OBTENER PROMOCIONES REINTEGRO

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var instancia = Funciones()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        var usuario = Usuario("1","Adam Bareiro", "adam9@gmail.com", "carlitos", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)


        coroutineScope.launch {
            try {
                var lista = instancia.obtenerPromocionesReintegro(usuario)
                for(promo in lista) {
                    Log.d("promo", "${ promo.titulo }")
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

BUSCAR PROMOCIONES POR RUBRO

        val mainScope = MainScope()
        mainScope.launch(Dispatchers.IO) {
            // Dentro de esta corrutina, puedes llamar a funciones suspendidas
            try {
                val instancia = leerId()
                val instanciaFuncion = Funciones()
                val usuario = instancia.obtenerUsuarioPorId("FDKPMRrqo1OnpxZQUVAYmHbywjw2")

                if (usuario != null) {
                    Log.d("Nombre Usuario", "${usuario.nombre}")
                } else {
                    // Usuario no encontrado
                }

                if (usuario != null) {
                    var promos = instanciaFuncion.buscarPromocionesPorRubro(usuario, "Educación")

                    for(promo in promos){
                        Log.d("Promos", "${promo.titulo}")
                    }
                }

            } catch (e: Exception) {
                Log.e("Error al obtener el usuario", e.message ?: "Error desconocido")
            }
        }

TRAER USUARIO ACTUAL

        val coroutineScope = CoroutineScope(Dispatchers.Main)
                val instancia = Funciones()
                coroutineScope.launch {
                    try {
                        val usuario = instancia.traerUsuarioActual()
                        if (usuario != null) {
                            Log.d("ID USUARIO", "${ usuario.id }")
                            Log.d("ID USUARIO", "${ usuario.nombre }")
                        }

                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }

TRAER LOGO COMERCIO

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val instancia = Funciones()
        coroutineScope.launch {
            try {
                val logo = instancia.traerLogoComercio("-Ne52vRSdRMlcb1J3OMT")
                if (logo != null) {
                    Log.d("logo", "${ logo }")
                }

            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }
        
        
TARJETAS COMUNES:
        var tarjetas2: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Ng")
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var usuario = Usuario("1","Adam Bareiro", "adam9@gmail.com", tarjetas, null, null, null, null)
        var promocion = Promocion("ads",null,null,null,null,null,null,null,
        tarjetas2,null,null,null,null,null,null,null,null,null)
        val instancia = Funciones()
        val lista = instancia.tarjetasComunes(usuario,promocion)
        if (lista.size>0){
            println(lista)
        }else{
            Log.d("No hay tarjetas comunes","No se encontraron tarjetas comunes entre el usuario y la promocion")
        }

 */

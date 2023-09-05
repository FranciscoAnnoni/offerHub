package com.example.offerhub
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Usuario{
    // Propiedades (atributos) de la clase
    var nombre: String
    var correo: String?
    var tarjetas: List<String?>?
    var favoritos: List<String?>?
    var wishlistComercio: List<String?>?
    var wishlistRubro: List<String?>?

    // Constructor primario
    constructor(nombre: String, correo: String?,tarjetas: List<String?>?, favoritos: List<String?>?, wishlistComercio: List<String?>?,  wishlistRubro: List<String?>?){
        this.nombre = nombre
        this.correo = correo
        this.tarjetas = tarjetas
        this.favoritos = favoritos
        this.wishlistComercio = wishlistComercio
        this.wishlistRubro = wishlistRubro
    }
}

class EscribirBD {

    fun escribirUsuariosEnFirebase(usuarios: List<Usuario>) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Usuario")

        usuarios.forEach { usuario ->
            val usuarioReferencia = referencia.push()
            usuarioReferencia.setValue(usuario)
        }
    }
}
//Ejemplo uso:
/*
        var instancia = EscribirBD()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDHfx9QyXuHQ_6ZpNC", "-NcDI2VVY0uVeOf4jzEI")
        var favoritos: List<String?> = listOf("-NcDJ22_da-2uuX7S2ZT", "-NcDLygyAWu4RhsKOa4Q", "-NcEA5nsLLY28LsQLjpP")
        var wishlistComercio: List<String?> = listOf("-NcDtqHdvwoX7wsvJHNw", "-NcE08fDeAQM2M98gbfR", "-NcE1qDB1J3N3w30hbUC")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        val usuariosNuevos = listOf(
            Usuario("Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro),
            Usuario("Nahuel Barrios", "perritogambeta@hotmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro)
        )
        instancia.escribirUsuariosEnFirebase(usuariosNuevos)
 */
package com.example.offerhub
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Usuario{
    // Propiedades (atributos) de la clase
    var nombre: String
    var correo: String?
    var tarjetas: List<String?>?

    // Constructor primario
    constructor(nombre: String, correo: String?,tarjetas: List<String?>?) {
        this.nombre = nombre
        this.correo = correo
        this.tarjetas = tarjetas
    }
}

class EscribirBD {

    fun escribirUsuariosEnFirebase(usuarios: List<Usuario>) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://oh-backend-848a1-default-rtdb.firebaseio.com/")
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
        var tarjetas: List<String?> = listOf("Rojo", "Verde", "Azul")
        val usuariosNuevos = listOf(
            Usuario("Pepe", "usuario1@example.com", tarjetas),
            Usuario("Jose", "usuario2@example.com", tarjetas)
        )
        instancia.escribirUsuariosEnFirebase(usuariosNuevos)
 */
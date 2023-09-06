package com.example.offerhub
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Usuario{
    // Propiedades (atributos) de la clase
    var id: String
    var nombre: String
    var correo: String?
    var contrasenia: String
    var tarjetas: List<String?>?
    var favoritos: List<String?>?
    var wishlistComercio: List<String?>?
    var wishlistRubro: List<String?>?
    var promocionesReintegro: List<String?>?

    // Constructor primario
    constructor(id: String, nombre: String, correo: String?, contrasenia: String, tarjetas: List<String?>?, favoritos: List<String?>?, wishlistComercio: List<String?>?,  wishlistRubro: List<String?>?, promocionesReintegro: List<String?>?){
        this.id = id
        this.nombre = nombre
        this.correo = correo
        this.contrasenia = contrasenia
        this.tarjetas = tarjetas
        this.favoritos = favoritos
        this.wishlistComercio = wishlistComercio
        this.wishlistRubro = wishlistRubro
        this.promocionesReintegro = promocionesReintegro
    }
}

class EscribirBD {

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

        val database = FirebaseDatabase.getInstance()
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
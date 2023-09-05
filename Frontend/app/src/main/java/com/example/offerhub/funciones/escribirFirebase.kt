package com.example.offerhub
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Usuario{
    // Propiedades (atributos) de la clase
    var nombre: String
    var correo: String?
    var tarjetas: List<String?>?
    var favoritos: List<String?>?
    var wishlistComercio: List<String?>?
    var wishlistRubro: List<String?>?
    var promocionesReintegro: List<String?>?

    // Constructor primario
    constructor(nombre: String, correo: String?,tarjetas: List<String?>?, favoritos: List<String?>?, wishlistComercio: List<String?>?,  wishlistRubro: List<String?>?, promocionesReintegro: List<String?>?){
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

    fun escribirUsuariosEnFirebase(usuarios: List<Usuario>) {
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com")
        val referencia: DatabaseReference = database.reference.child("/Usuario")

        usuarios.forEach { usuario ->
            val usuarioReferencia = referencia.push()
            usuarioReferencia.setValue(usuario)
        }
    }

//    A PARTIR DE ACA SIRVE PARA EDITAR LOS OBJETOS DE LA BASE DE DATOS

    fun agregarElementoAListas(userId: String, elementoId: String, clase: String, nombreLista: String) {
        val database = FirebaseDatabase.getInstance("https://oh-bkd2-default-rtdb.firebaseio.com")
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
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDHfx9QyXuHQ_6ZpNC", "-NcDI2VVY0uVeOf4jzEI")
        var favoritos: List<String?> = listOf("-NcDJ22_da-2uuX7S2ZT", "-NcDLygyAWu4RhsKOa4Q", "-NcEA5nsLLY28LsQLjpP")
        var wishlistComercio: List<String?> = listOf("-NcDtqHdvwoX7wsvJHNw", "-NcE08fDeAQM2M98gbfR", "-NcE1qDB1J3N3w30hbUC")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDtqHdvwoX7wsvJHNw")
        val usuariosNuevos = listOf(
            Usuario("Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro),
            Usuario("Nahuel Barrios", "perritogambeta@hotmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)
        )
        instancia.escribirUsuariosEnFirebase(usuariosNuevos)





 */
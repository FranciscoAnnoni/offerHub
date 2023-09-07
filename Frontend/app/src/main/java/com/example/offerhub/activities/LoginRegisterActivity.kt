package com.example.offerhub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Sucursal
import com.example.offerhub.Usuario
import com.example.offerhub.leerId
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        // Crear una instancia de MainScope para ejecutar corrutinas en el hilo principal
        val mainScope = MainScope()
        mainScope.launch(Dispatchers.IO) {
            // Dentro de esta corrutina, puedes llamar a funciones suspendidas
            try {
                val instancia = leerId()
                val instanciaFuncion = Funciones()
                val usuario = instancia.obtenerUsuarioPorId("-Ndg5uxYvmAkEIpthvNQ")

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


    }
}
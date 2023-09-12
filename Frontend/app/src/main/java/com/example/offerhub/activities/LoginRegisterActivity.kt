package com.example.offerhub.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Sucursal
import com.example.offerhub.Usuario
import com.example.offerhub.leerId
import com.google.firebase.auth.FirebaseAuth
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

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val instancia = LecturaBD()
        coroutineScope.launch {
            try {
                val promos = instancia.obtenerPromosPorTarjeta("-Ne52v1WN0OfZwqqBx_H")
                if (promos != null) {
                    for(promo in promos) {
                        Log.d("logo", "${promo.logo}")
                    }
                }

            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }


    }
}
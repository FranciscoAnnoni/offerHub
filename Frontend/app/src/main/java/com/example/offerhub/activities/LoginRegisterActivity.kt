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
        var instancia = Funciones()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        var usuario = Usuario("FDKPMRrqo1OnpxZQUVAYmHbywjw2","Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)

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

    }
}
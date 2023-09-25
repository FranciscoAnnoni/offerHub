package com.example.offerhub.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.databinding.ActivityPromoNotiDetailBinding
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromoNotiDetailActivity() : AppCompatActivity() {

    val binding by lazy {
        ActivityPromoNotiDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Puedes establecer el diseño o contenido que deseas mostrar aquí
        setContentView(binding.root)

        val prefs = getSharedPreferences("NotiReintegro", Context.MODE_PRIVATE)
        val promocionId = prefs.getString("promocion", null)

        if (promocionId != null){
            CoroutineScope(Dispatchers.Main).launch {
                val promocion=LeerId().obtenerPromocionPorId(promocionId)
                if(promocion!=null){
                    binding.promoTitulo.text=promocion.titulo
                    binding.promoDesc.text=promocion.descripcion
                }
            }

        }


        // Si necesitas recuperar información de la notificación, puedes hacerlo así:


    }

}

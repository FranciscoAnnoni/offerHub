package com.example.offerhub.activities

import androidx.navigation.fragment.findNavController
import TarjetasPromocionAdapter
import UserViewModel
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.databinding.ActivityPromoNotiDetailBinding
import com.example.offerhub.funciones.getContrastColor
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.funciones.obtenerColorMayoritario
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromoNotiDetailActivity() : AppCompatActivity() {
    var isFavorite = false
    var isTyCExpanded = false
    var userViewModel :UserViewModel=UserViewModel()

    val binding by lazy {
        ActivityPromoNotiDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Puedes establecer el diseño o contenido que deseas mostrar aquí
        setContentView(binding.root)

        val prefs = getSharedPreferences("NotiReintegro", Context.MODE_PRIVATE)
        val promocionId = prefs.getString("promocion", null)
        var promocion: Promocion = Promocion()
        val activityContext = this
        CoroutineScope(Dispatchers.Main).launch {
            promocion = promocionId?.let { LeerId().obtenerPromocionPorId(it) }!!


            val instancia = Funciones()
            userViewModel = UserViewModelSingleton.getUserViewModel()

            binding.imageClose.setOnClickListener {
                //LLEVAR DONDE CORRESPONDA
            }
            val iconoEnlace = findViewById<ImageView>(R.id.icono_enlace)

            // Agrega un OnClickListener al ImageView
            iconoEnlace.setOnClickListener {
                // Define el enlace que deseas abrir en el navegador
                val url = promocion.url // Reemplaza con tu enlace real
                // Crea un Intent para abrir el enlace en un navegador externo
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                // Verifica si hay una actividad que pueda manejar el intent (navegador)
                if (intent.resolveActivity(activityContext.packageManager) != null) {
                    startActivity(intent)
                }
                else {
                    // Maneja el caso en el que no se pueda abrir el navegador
                    Toast.makeText(this@PromoNotiDetailActivity, "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show()
                }
            }
            val recyclerViewTarjetas = findViewById<RecyclerView>(R.id.recyclerViewTarjetas)

            val coroutineScope = CoroutineScope(Dispatchers.Main)
            fun comparar(it: Promocion): Boolean {
                return it.id == promocion.id
            }
            coroutineScope.launch {
                var tarjetasComunes = instancia.tarjetasComunes(userViewModel.usuario, promocion)
                val adapter = TarjetasPromocionAdapter(tarjetasComunes as List<String>?)

                recyclerViewTarjetas.layoutManager = LinearLayoutManager(this@PromoNotiDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewTarjetas.adapter = adapter
                val isFavorite = userViewModel.favoritos.any{ it -> comparar(it) }
                binding.imageFav.setImageResource(getFavResource(isFavorite))
            }

            binding.imageFav.setOnClickListener {
                fun isFavorite(): Boolean {
                    return userViewModel.favoritos.any { it -> comparar(it) }
                }
                Log.d("Cant de FAVORITOS",userViewModel.favoritos.size.toString())
                Log.d("FAVORITOS",userViewModel.favoritos.joinToString(","))
                // Cambiar la imagen según el estado
                if (!isFavorite()) {
                    userViewModel.favoritos.add(promocion)
                    UserViewModelCache().guardarUserViewModel(userViewModel)
                    Log.d("Agrego Favoritos",UserViewModelSingleton.getSingleUserViewModel()!!.favoritos.size.toString())
                    coroutineScope.launch {
                        instancia.agregarPromocionAFavoritos(
                            userViewModel.usuario!!.id,
                            promocion.id.toString()
                        )


                    }
                } else {
                    userViewModel.favoritos.removeIf { it->comparar(it) }
                    UserViewModelCache().guardarUserViewModel(userViewModel)
                    Log.d("Saco Favoritos",userViewModel.favoritos.size.toString())
                    coroutineScope.launch {
                        instancia.elimiarPromocionDeFavoritos(
                            userViewModel.usuario!!.id,
                            promocion.id.toString()
                        )
                    }
                }

                // Establecer la imagen en la ImageView
                binding.imageFav.setImageResource(getFavResource(isFavorite()))
            }
            binding.apply {
                var text =promocion.obtenerDesc()
                promoBenef.text = text
                if(promocion.tipoPromocion=="Reintegro" || promocion.tipoPromocion=="Descuento"){
                    promoBenefTipo.text="%"
                } else if (promocion.tipoPromocion=="2x1") {
                    promoBenefTipo.visibility= View.GONE
                } else if (promocion.tipoPromocion=="Cuotas"){
                    promoBenefTipo.text= "cuotas \nsin interés"
                    val marginLeftInDp = 5f // 5dp
                    val scale = resources.displayMetrics.density
                    val marginLeftInPixels = (marginLeftInDp * scale + 0.5f).toInt()
                    val params = promoBenefTipo.layoutParams as ViewGroup.MarginLayoutParams
                    params.marginStart = marginLeftInPixels
                    promoBenefTipo.layoutParams = params
                    promoBenefTipo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                }
                promoTitulo.text = promocion.titulo?.substringAfter(":")?.trim()
                if((promocion.descripcion.toString()).length>0) {
                    promoDesc.text = promocion.descripcion
                    promoDesc.visibility = View.VISIBLE
                }
                if(promocion.tipoPromocion!="Reintegro") {
                    containerTope.visibility = View.GONE
                } else {
                    txtTope.text=promocion.topeTexto
                }
                promoVigencia.text=promocion.obtenerTextoVigencia()
                coroutineScope.launch {
                    promoComercio.text = Funciones().traerInfoComercio(promocion.comercio,"nombre")
                    val logoBitmap = Comercio(
                        "",
                        "",
                        "","",""
                    ).base64ToBitmap(Funciones().traerLogoComercio(promocion.comercio))
                    if (logoBitmap != null) {
                        viewPagerProductImages.setImageBitmap(logoBitmap)
                        val color= obtenerColorMayoritario(logoBitmap)
                        // Suponiendo que ya tienes el color mayoritario en la variable colorMayoritario
                        val cardProductImages = findViewById<CardView>(R.id.cardProductImages)

// Establece el color de fondo del CardView
                        cardProductImages.setBackgroundColor(color)
                        val textColor = getContrastColor(color)
                        imageClose.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)
                        imageFav.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)

                    }
                }
                var lista= listOf<String?>()
                if(promocion.dias ==null) {
                    lista= listOf("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo")
                } else {
                    lista= promocion.dias!!
                }
                for (dia in lista) {
                    val nombreDia = removeAccents(dia)
                    val circleId = resources.getIdentifier(
                        "circle$nombreDia",
                        "id",
                        this@PromoNotiDetailActivity.packageName
                    )
                    val circleView = findViewById<TextView>(circleId)
                    circleView.setBackgroundResource(R.drawable.circle_foreground)
                    circleView.setTextColor(Color.parseColor("#FFFFFF"))
                }

            }

        }

    }
}

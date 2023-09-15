package com.example.offerhub.fragments.shopping

import TarjetasPromocionAdapter
import UserViewModel
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentPromoDetailBinding
import com.example.offerhub.funciones.getContrastColor
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.funciones.obtenerColorMayoritario
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.PromoDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromoDetailFragment: Fragment(R.layout.fragment_promo_detail){
    private val args by navArgs<PromoDetailFragmentArgs>()
    private lateinit var binding: FragmentPromoDetailBinding
    private val viewModel by viewModels<PromoDetailViewModel>()
    var isFavorite = false
    var isNotificado = false
    var isTyCExpanded = false
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // hideBottomNavigationView()
        binding = FragmentPromoDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promocion = args.promocion
        val instancia = Funciones()
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tituloTyc.setOnClickListener {
            isTyCExpanded = !isTyCExpanded
            if (isTyCExpanded) {
                binding.imageToggleTyC.setImageResource(R.drawable.ic_expand_less)
                binding.promoTyC.visibility = View.VISIBLE
                // Calcula la posición y anima el desplazamiento hacia abajo
                val animator = ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.topLine.bottom)
                animator.duration = 500 // Duración de la animación en milisegundos
                animator.start()
            } else {
                binding.imageToggleTyC.setImageResource(R.drawable.ic_expand_more)
                binding.promoTyC.visibility = View.GONE
            }
        }

        val iconoEnlace = view.findViewById<ImageView>(R.id.icono_enlace)

        // Agrega un OnClickListener al ImageView
        iconoEnlace.setOnClickListener {
            // Define el enlace que deseas abrir en el navegador
            val url = promocion.url // Reemplaza con tu enlace real
            // Crea un Intent para abrir el enlace en un navegador externo
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            // Verifica si hay una actividad que pueda manejar el intent (navegador)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Maneja el caso en el que no se pueda abrir el navegador
                Toast.makeText(requireContext(), "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show()
            }
        }
        val recyclerViewTarjetas = view.findViewById<RecyclerView>(R.id.recyclerViewTarjetas)

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            var tarjetasComunes = instancia.tarjetasComunes(instancia.traerUsuarioActual(), promocion)
            val adapter = TarjetasPromocionAdapter(tarjetasComunes as List<String>?)

            recyclerViewTarjetas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewTarjetas.adapter = adapter
            isFavorite = instancia.traerUsuarioActual()
                ?.let { instancia.existePromocionEnFavoritos(it, promocion.id) } == true
            isNotificado = instancia.traerUsuarioActual()
                ?.let { instancia.existePromocionEnReintegros(it, promocion.id) } == true
            binding.imageFav.setImageResource(getFavResource(isFavorite))
            binding.btnNotificar.text=if (isNotificado) "Eliminar Notificacion" else "Notificar"
        }
        binding.imageFav.setOnClickListener {
            isFavorite = !isFavorite // Cambiar el estado al contrario

            // Cambiar la imagen según el estado
            if (isFavorite) {
                userViewModel.favoritos.add(promocion)
                coroutineScope.launch {
                    instancia.agregarPromocionAFavoritos(
                        userViewModel.id.toString(),
                        promocion.id.toString()
                    )
                }
            } else {
                coroutineScope.launch {
                    userViewModel.favoritos.remove(promocion)
                    instancia.elimiarPromocionDeFavoritos(
                        userViewModel.id.toString(),
                        promocion.id.toString()
                    )
                }
            }

            // Establecer la imagen en la ImageView
            binding.imageFav.setImageResource(getFavResource(isFavorite))
        }
        binding.btnNotificar.setOnClickListener {
            isNotificado = !isNotificado // Cambiar el estado al contrario

            // Cambiar la imagen según el estado
            if (isNotificado) {
                coroutineScope.launch {
                    instancia.agregarPromocionAReintegro(
                        userViewModel.id.toString(),
                        promocion.id.toString()
                    )
                }
            } else {
                coroutineScope.launch {
                    instancia.elimiarPromocionDeReintegro(
                        userViewModel.id.toString(),
                        promocion.id.toString()
                    )
                }
            }

            // Establecer la imagen en la ImageView
            binding.btnNotificar.text=if (isNotificado) "Eliminar Notificacion" else "Notificar"
        }

        binding.apply {
            var text =promocion.obtenerDesc()
            promoBenef.text = text
            if(promocion.tipoPromocion=="Reintegro" || promocion.tipoPromocion=="Descuento"){
                promoBenefTipo.text="%"
            } else if (promocion.tipoPromocion=="2x1") {
                promoBenefTipo.visibility=View.GONE
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
                containerNotificar.visibility = View.GONE
                containerTope.visibility = View.GONE
            } else {
                txtTope.text=promocion.topeTexto
            }
            promoTyC.text = promocion.tyc
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
                    val color=obtenerColorMayoritario(logoBitmap)
                    // Suponiendo que ya tienes el color mayoritario en la variable colorMayoritario
                    val cardProductImages = view.findViewById<CardView>(R.id.cardProductImages)

// Establece el color de fondo del CardView
                    cardProductImages.setBackgroundColor(color)
                    val textColor = getContrastColor(color)
                    imageClose.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)
                    imageFav.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)

                }
            }
            for (dia in promocion.dias!!) {
                val nombreDia= removeAccents(dia)
                val circleId = resources.getIdentifier("circle$nombreDia", "id", requireContext().packageName)
                val circleView = view.findViewById<TextView>(circleId)
                circleView.setBackgroundResource(R.drawable.circle_foreground)
                circleView.setTextColor(Color.parseColor("#FFFFFF"))
            }
            }
            //tvProductPrice.text = "$ ${promocion.price}"
            //tvProductDescription.text = promocion.description

        }


    }
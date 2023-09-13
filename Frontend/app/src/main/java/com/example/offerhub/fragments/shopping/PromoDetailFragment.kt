package com.example.offerhub.fragments.shopping

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentPromoDetailBinding
import com.example.offerhub.funciones.getContrastColor
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
        val instancia=Funciones()
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            isFavorite= instancia.traerUsuarioActual()
                ?.let { instancia.existePromocionEnFavoritos(it,promocion.id) } == true
            binding.imageFav.setImageResource(getFavResource())
        }
        binding.imageFav.setOnClickListener {
            isFavorite = !isFavorite // Cambiar el estado al contrario

            // Cambiar la imagen según el estado
            var favoriteImageResource =R.drawable.ic_fav
            if (isFavorite) {
                coroutineScope.launch {
                    instancia.agregarPromocionAFavoritos(
                        instancia.traerUsuarioActual()?.id.toString(),
                        promocion.id.toString()
                    )
                }
            } else {
                coroutineScope.launch {
                    instancia.elimiarPromocionDeFavoritos(
                        instancia.traerUsuarioActual()?.id.toString(),
                        promocion.id.toString()
                    )
                }
            }

            // Establecer la imagen en la ImageView
            binding.imageFav.setImageResource(getFavResource())
        }

        binding.apply {
            var text =promocion.obtenerDesc()
            promoTitulo.text = text
            promoDesc.text = promocion.tyc
            coroutineScope.launch {
                promoComercio.text = Funciones().traerInfoComercio(promocion.comercio,"nombre")
                val logoBitmap = Comercio(
                    "",
                    "",
                    "",
                    ""
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

    fun getFavResource(): Int {
        if (isFavorite) {
            return R.drawable.ic_fav_selected
        } else {
            return R.drawable.ic_fav
        }
    }

    }
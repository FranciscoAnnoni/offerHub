package com.example.offerhub.fragments.partners
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.activities.SucursalesAdapter
import com.example.offerhub.databinding.FragmentPromoDetailPartnersBinding
import com.example.offerhub.funciones.getContrastColor
import com.example.offerhub.funciones.obtenerColorMayoritario
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromoDetailPartnersFragment: Fragment(R.layout.fragment_promo_detail_partners){
    private val args by navArgs<PromoDetailPartnersFragmentArgs>()
    private lateinit var binding: FragmentPromoDetailPartnersBinding
    var isFavorite = false
    var isNotificado = false
    var isTyCExpanded = false
    var isSucursalesExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // hideBottomNavigationView()
        binding = FragmentPromoDetailPartnersBinding.inflate(inflater)
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
        binding.sucursalesHeader.setOnClickListener {
            isSucursalesExpanded = !isSucursalesExpanded
            Log.d("Sucursales", promocion.sucursales?.size.toString())
            if (isSucursalesExpanded) {
                binding.imageToggleSucursales.setImageResource(R.drawable.ic_expand_less)
                binding.recyclerViewSucursales.visibility = View.VISIBLE
                // Calcula la posición y anima el desplazamiento hacia abajo
                val animator = ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.topLine.bottom)
                animator.duration = 500 // Duración de la animación en milisegundos
                animator.start()
            } else {
                binding.imageToggleSucursales.setImageResource(R.drawable.ic_expand_more)
                binding.recyclerViewSucursales.visibility = View.GONE
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
        fun comparar(it:Promocion): Boolean {
            return it.id == promocion.id
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
                containerTope.visibility = View.GONE
            } else {
                txtTope.text=promocion.topeTexto
            }

            promoTyC.text = promocion.tyc
            promoVigencia.text=promocion.obtenerTextoVigencia()
            coroutineScope.launch {
                val sucursales = promocion.sucursales ?: emptyList() // Asume que `sucursales` es una lista de Strings en tu objeto `promocion`
                if(sucursales.size>0) {
                    val sucursalAdapter = SucursalesAdapter(sucursales)
                    recyclerViewSucursales.adapter = sucursalAdapter
                    binding.sucursalesSection.visibility=View.VISIBLE
                } else {
                    binding.sucursalesSection.visibility=View.GONE
                }
            }

            coroutineScope.launch {
                promoComercio.text = Funciones().traerInfoComercio(promocion.comercio,"nombre")
                val logoBitmap = Comercio().base64ToBitmap(Funciones().traerLogoComercio(promocion.comercio))
                if (logoBitmap != null) {
                    viewPagerProductImages.setImageBitmap(logoBitmap)
                    val color=obtenerColorMayoritario(logoBitmap)
                    // Suponiendo que ya tienes el color mayoritario en la variable colorMayoritario
                    val cardProductImages = view.findViewById<CardView>(R.id.cardProductImages)

// Establece el color de fondo del CardView
                    cardProductImages.setBackgroundColor(color)
                    val textColor = getContrastColor(color)
                    imageClose.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)

                }
            }
            var listaDefault= mapOf("LU" to "Lunes", "MA" to "Martes", "MI" to "Miercoles", "JU" to "Jueves", "VI" to "Viernes", "SA" to "Sabado", "DO" to "Domingo")
            var lista= listOf<String?>()
            if(promocion.dias !=null) {
                lista= promocion.dias as List<String>
            } else {
                lista=listaDefault.values.toList()
            }
                for (diaLista in lista) {
                   var dia: String=""
                    if(!listaDefault.values.any({it->diaLista==it})){
                        dia=listaDefault[diaLista].toString()
                    } else {
                        dia=diaLista
                    }
                    val nombreDia = removeAccents(dia)
                    val circleId = resources.getIdentifier(
                        "circle$nombreDia",
                        "id",
                        requireContext().packageName
                    )
                    val circleView = view.findViewById<TextView>(circleId)
                    if(circleView!=null) {
                        circleView.setBackgroundResource(R.drawable.circle_foreground)
                        circleView.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }

            }
            //tvProductPrice.text = "$ ${promocion.price}"
            //tvProductDescription.text = promocion.description

        }
    }
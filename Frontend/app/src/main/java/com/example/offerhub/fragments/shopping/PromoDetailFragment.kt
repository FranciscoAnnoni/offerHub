package com.example.offerhub.fragments.shopping

import TarjetasPromocionAdapter
import UserViewModel
import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.activities.SucursalesAdapter
import com.example.offerhub.databinding.FragmentPromoDetailBinding
import com.example.offerhub.funciones.AlarmaNotificacion
import com.example.offerhub.funciones.CanalNoti
import com.example.offerhub.funciones.getContrastColor
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.funciones.obtenerColorMayoritario
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class PromoDetailFragment: Fragment(R.layout.fragment_promo_detail){
    private val args by navArgs<PromoDetailFragmentArgs>()
    private lateinit var binding: FragmentPromoDetailBinding
    var isFavorite = false
    var isNotificado = false
    var isTyCExpanded = false
    var isSucursalesExpanded = false
    var userViewModel :UserViewModel=UserViewModel()

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
        userViewModel = UserViewModelSingleton.getUserViewModel()
        val promocion = args.promocion
        val instancia = Funciones()
        val instanciaCanal = CanalNoti()
        getContext()?.let { instanciaCanal.createChannel(it) }
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
        binding.imageToggleSucursales.setOnClickListener {
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
        coroutineScope.launch {
            var tarjetasComunes = instancia.tarjetasComunes(userViewModel.usuario, promocion)
            val adapter = TarjetasPromocionAdapter(tarjetasComunes as List<String>?)

            recyclerViewTarjetas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewTarjetas.adapter = adapter
            val isFavorite = userViewModel.favoritos.any{ it -> comparar(it) }
            val isNotificado = userViewModel.reintegros.any{ it -> comparar(it) }
            binding.imageFav.setImageResource(getFavResource(isFavorite))
            binding.btnNotificar.text=if (isNotificado) "Eliminar Notificacion" else "Notificar"
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
        binding.btnNotificar.setOnClickListener {
            // Cambiar la imagen según el estado
            Log.d("Cant de Reintegros",userViewModel.reintegros.size.toString())
            Log.d("Reintegros",userViewModel.reintegros.joinToString(","))

            coroutineScope.launch {
                val intent = Intent(context, AlarmaNotificacion::class.java).apply{
                    putExtra("comercio", Funciones().traerInfoComercio(promocion.comercio,"nombre"))
                    putExtra("promocion",promocion.id.toString())
                }

                fun isNotificado(): Boolean {
                    return userViewModel.reintegros.any { it -> comparar(it) }
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    AlarmaNotificacion.NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                if (!isNotificado()) {
                    userViewModel.reintegros.add(promocion)
                    UserViewModelCache().guardarUserViewModel(userViewModel)
                    Log.d("Agrego Reintegros",userViewModel.reintegros.size.toString())

                    // Cambiar la imagen según el estado

                    Log.d("entre","entre")
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + (5*1000), pendingIntent) //a los 30 segundos

                    instancia.agregarPromocionAReintegro(
                        userViewModel.id.toString(),
                        promocion.id.toString(),
                    )
                } else {
                    userViewModel.reintegros.removeIf { it->comparar(it) }
                    UserViewModelCache().guardarUserViewModel(userViewModel)
                    Log.d("Saco Reintegros",userViewModel.reintegros.size.toString())
                    coroutineScope.launch {
                        instancia.elimiarPromocionDeReintegro(
                            userViewModel.id.toString(),
                            promocion.id.toString()
                        )
                        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.cancel(pendingIntent)
                    }
                }
            // Establecer la imagen en la ImageView
                binding.btnNotificar.text=if (isNotificado()) "Eliminar Notificacion" else "Notificar"
            }
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
                Log.d("PROMO DETAIL sucursales",promocion.sucursales!!.size.toString())
                val sucursales = promocion.sucursales ?: emptyList() // Asume que `sucursales` es una lista de Strings en tu objeto `promocion`
                val sucursalAdapter = SucursalesAdapter(sucursales)
                recyclerViewSucursales.adapter = sucursalAdapter
            }

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
            var lista= listOf<String?>()
            if(promocion.dias ==null) {
                lista= listOf("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo")
            } else {
                lista=promocion.dias
            }
                for (dia in lista) {
                    val nombreDia = removeAccents(dia)
                    val circleId = resources.getIdentifier(
                        "circle$nombreDia",
                        "id",
                        requireContext().packageName
                    )
                    val circleView = view.findViewById<TextView>(circleId)
                    circleView.setBackgroundResource(R.drawable.circle_foreground)
                    circleView.setTextColor(Color.parseColor("#FFFFFF"))
                }

            }
            //tvProductPrice.text = "$ ${promocion.price}"
            //tvProductDescription.text = promocion.description

        }
    }
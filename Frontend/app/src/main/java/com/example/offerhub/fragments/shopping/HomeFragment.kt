package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import android.os.Handler
import android.os.Looper
import PromocionGridPorCategoriaAdapter
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.CheckBox
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentHomeBinding
import com.example.offerhub.interfaces.FilterData
import com.example.offerhub.interfaces.PromocionFragmentListener
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.internal.ViewUtils.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.ceil
import java.lang.Math.round


class HomeFragment : Fragment(R.layout.fragment_home), PromocionFragmentListener {
    private lateinit var binding: FragmentHomeBinding

    private var scrollPosition: Int = 0
    var isFavorite = false
    var listenerHabilitado = false
    private fun showToast(message: String, duration: Long) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()

        // Oculta el mensaje después del tiempo especificado
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ toast.cancel() }, duration)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun mostrarAvisoSobreeleccion() {
        showToast("El limite de seleccion son 2 promociones.", 10000)
    }
    override fun updateButtonVisibility(shouldBeVisible: Boolean) {
        binding.btnComparar.visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var funciones = Funciones()

        val categoriasContainer = view.findViewById<LinearLayout>(R.id.categoriasContainer)
        val promosContainer = view.findViewById<LinearLayout>(R.id.containerPromos)
        val homeScrollView = view.findViewById<ScrollView>(R.id.homeScrollView)
        val listView = view.findViewById<GridView>(R.id.promocionesGridView)
        val mySwitch = view.findViewById<ImageView>(R.id.switchHomeMode)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val userViewModel = UserViewModelSingleton.getUserViewModel()
        fun setearIconoHome(homeModeFull: Boolean) {

            if(homeModeFull){
                mySwitch.setImageResource(R.drawable.ic_tablerow)
            } else {
                mySwitch.setImageResource(R.drawable.ic_gridview)
            }
        }
        fun mostrarCargarTarjetas(){
            val cargarTarjetas = view.findViewById<LinearLayout>(R.id.llCargarTarjetas)
            cargarTarjetas.visibility = View.VISIBLE
            binding.btnComparar.visibility=View.GONE
            binding.botonGuardar.visibility = View.GONE

        }
        fun cargarVista() {
            val cargarTarjetas = view.findViewById<LinearLayout>(R.id.llCargarTarjetas)
            cargarTarjetas.visibility = View.GONE
            if (userViewModel.usuario!!.homeModoFull=="1") {
                promosContainer.visibility=View.VISIBLE
                homeScrollView.visibility = View.GONE
                listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                binding.botonGuardar.visibility = View.VISIBLE
                binding.btnComparar.visibility=View.GONE
                var checkPrendido: Boolean = false
                // Llamar a la función que obtiene los datos.
                coroutineScope.launch {
                    try {
                        var promocionesOrdenadas=userViewModel.listadoDePromosDisp.sortedBy { it.titulo!!.lowercase() }
                        if(promocionesOrdenadas.isEmpty()){
                            mostrarCargarTarjetas()
                            val switch = view.findViewById<ImageView>(R.id.switchHomeMode)
                            switch.visibility = View.GONE
                        }
                        Log.d("Cantidad promos ", promocionesOrdenadas.size.toString())
                        val adapter = PromocionGridAdapter(view.context, listOf(),this@HomeFragment)
                        adapter.promocionesTotales= promocionesOrdenadas as MutableList<Promocion>
                        adapter.cargarMasPromociones()
                        adapter.eliminarLista()
                        adapter.setFragment(this@HomeFragment)
                        listView.adapter = adapter
                        binding.botonGuardar.setOnClickListener{
                            checkPrendido = !checkPrendido

                            adapter.setCheckBoxesVisibility(checkPrendido)
                            if (checkPrendido){

                                val colorFondo = ContextCompat.getColor(requireContext(), R.color.g_gray500)
                                val colorStateList = ColorStateList.valueOf(colorFondo)

// Establece el color de fondo en la vista.
                                binding.botonGuardar.backgroundTintList = colorStateList
                            }else{
                                binding.btnComparar.visibility=View.GONE
                                val colorFondo = ContextCompat.getColor(requireContext(), R.color.white)
                                val colorStateList = ColorStateList.valueOf(colorFondo)

// Establece el color de fondo en la vista.
                                binding.botonGuardar.backgroundTintList = colorStateList
                                adapter.eliminarLista()
                            }
                        }

                        binding.btnComparar.setOnClickListener {
                            var promos =adapter.getSeleccion()
                            var promo1 = promos[0]
                            var promo2 = promos[1]
                            val bottomSheetDialog = CompararFragment.newInstance(promo1, promo2)
                            bottomSheetDialog.show(requireActivity().supportFragmentManager, "CompararFragment")
                        }


                        listView.setOnItemClickListener { parent, _, position, _ ->
                            val selectedPromo =
                                adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToPromoDetailFragment(
                                    selectedPromo
                                )
                            findNavController().navigate(action)
                        }
                        listView.visibility = View.VISIBLE

                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }
            } else {

                binding.btnComparar.visibility=View.GONE
                // listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()
                var cantPromos=0
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {
                    // Seteo Visibilidad
                    homeScrollView.visibility = View.VISIBLE
                    categoriasContainer.visibility = View.GONE
                    promosContainer.visibility = View.GONE
                    categoriasContainer.visibility = View.GONE
                    //Obtengo el listado de categorias.
                    var categorias: MutableList<Categoria> = mutableListOf<Categoria>()
                    categorias = funciones.obtenerCategorias(view.context) as MutableList<Categoria>
                    categoriasContainer.removeAllViews()
                    for (categoria in categorias) {
                        // Crea un título de categoría
                        val promocionesDesordenadas: List<Promocion> =
                            userViewModel.listadoDePromosDisp.filter { it.categoria == categoria.nombre }.take(11)
                        var promociones=promocionesDesordenadas.sortedBy { it.titulo!!.lowercase() }
                        if (promociones.isNotEmpty()) {
                            val promosDispo = view.findViewById<TextView>(R.id.tvPromocionesDisponibles)
                            promosDispo.visibility = View.VISIBLE
                            val switch = view.findViewById<ImageView>(R.id.switchHomeMode)
                            switch.visibility = View.VISIBLE
                            cantPromos+=promociones.size

                            if (requireContext() != null) {
                                val categoriaTitle = TextView(requireContext())
                                categoriaTitle.text = categoria.nombre
                                categoriaTitle.textSize = 20f
                                categoriaTitle.setPadding(0, 16, 0, 8)

                                // Crea un RecyclerView para esta categoría
                                val recyclerView = RecyclerView(requireContext())
                                recyclerView.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    RecyclerView.HORIZONTAL,
                                    false
                                )

                                // Configura el adaptador para el RecyclerView
                                val adapter = PromocionGridPorCategoriaAdapter(
                                    activity!!,
                                    promociones,
                                    object : PromocionGridPorCategoriaAdapter.OnItemClickListener {
                                        override fun onItemClick(promocion: Promocion) {
                                            // Maneja el clic en el elemento aquí
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToPromoDetailFragment(
                                                    promocion
                                                )
                                            findNavController().navigate(action)
                                        }

                                        override fun onVerMasClick() {
                                            TODO("Not yet implemented")
                                        }
                                    },
                                    object : PromocionGridPorCategoriaAdapter.OnItemClickListener {
                                        override fun onItemClick(promocion: Promocion) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onVerMasClick() {
                                            //findNavController().navigate(R.id.favFragment)
                                           var navController=requireActivity().findNavController(R.id.mainAppFragment)
                                            navController.popBackStack(R.id.homeFragment, false);
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                                                    categoria.nombre
                                                )
                                            navController.navigate(action);
                                            // Maneja el clic en el elemento aquí
                                            /*
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                                                    categoria.nombre
                                                )
                                            findNavController().navigate(action)

                                             */
                                        }

                                    })
                                recyclerView.adapter = adapter

                                binding.botonGuardar.visibility = View.GONE


                                // Agrega el título y el RecyclerView al contenedor
                                categoriasContainer.addView(categoriaTitle)
                                categoriasContainer.addView(recyclerView)

                            }
                        } else {
                            val promosDispo = view.findViewById<TextView>(R.id.tvPromocionesDisponibles)
                            promosDispo.visibility = View.GONE
                        }
                    }
                    if(cantPromos==0){
                        mostrarCargarTarjetas()
                        val switch = view.findViewById<ImageView>(R.id.switchHomeMode)
                        switch.visibility = View.GONE
                    }
                    categoriasContainer.visibility = View.VISIBLE
                }
            }
        }
        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE

            if(userViewModel.usuario==null){
                userViewModel.usuario = Funciones().traerUsuarioActual()
            }else {
                Log.d("YA Existe usuario 123221",userViewModel.usuario!!.nombre)
            }
            Log.d("Logueandome",userViewModel.usuario!!.nombre)
            Log.d("Logueandome",userViewModel.listadoDePromosDisp.count().toString())
            Log.d("Logueandome",userViewModel.favoritos.count().toString())
            listenerHabilitado=false
            setearIconoHome(userViewModel.usuario!!.homeModoFull=="1")
            listenerHabilitado=true
            progressBar.visibility = View.GONE
        }.invokeOnCompletion {
            cargarVista()
        }




        mySwitch.setOnClickListener {
            if(listenerHabilitado){

            val coroutineScope = CoroutineScope(Dispatchers.Main)

            // Llamar a la función que obtiene los datos.
            val job = coroutineScope.launch {
                userViewModel.usuario!!.homeModoFull=if(userViewModel.usuario!!.homeModoFull=="1") "0" else "1"
                UserViewModelCache().guardarUserViewModel(userViewModel)
                EscribirBD().editarAtributoDeClase("Usuario",
                    userViewModel.usuario!!.id.toString(),"homeModoFull",userViewModel.usuario!!.homeModoFull.toString())
                setearIconoHome(userViewModel.usuario!!.homeModoFull=="1")
                cargarVista()
            }
        }
        }
        val lupa = view.findViewById<ImageView>(R.id.logoLupa)
        val cerrar = view.findViewById<ImageView>(R.id.logoCerrar)

        binding.buscadores.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verifica si el texto ha cambiado y muestra u oculta el logoClose

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    cerrar.visibility = View.GONE
                } else {
                    cerrar.visibility = View.VISIBLE
                }
            }
        })

        binding.logoLupa.setOnClickListener {
            val textoBusqueda = binding.buscadores.text.toString()
            if (textoBusqueda.length > 0) {
                var navController=requireActivity().findNavController(R.id.mainAppFragment)
                navController.popBackStack(R.id.homeFragment, false);
                val action =
                    HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                        textoBusqueda
                    )
                navController.navigate(action);
            }
        }

        binding.logoCerrar.setOnClickListener {
            binding.buscadores.setText("")
            hideKeyboard(view)
        }
        binding.buscadores.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val textoBusqueda = binding.buscadores.text.toString()
                if (textoBusqueda.length > 0) {
                    var navController=requireActivity().findNavController(R.id.mainAppFragment)
                    navController.popBackStack(R.id.homeFragment, false);
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                            textoBusqueda
                        )
                    navController.navigate(action);
                }
            }
            //hideKeyboard()
            false
        }
        val cargarTarjetas = view.findViewById<LinearLayout>(R.id.llCargarTarjetas)
        cargarTarjetas.setOnClickListener {
            Log.d("Estoy adentro del click de agregar tarjetas", "click click click")
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCargarTarjetasFragment())
        }

    }

    fun calcularAlturaTotalGridView(gridView: GridView): Int {
        val adapter = gridView.adapter
        var alturaTotal = 0

        for (i in 0 until ceil((adapter.count/2).toDouble()).toInt()) {
            val itemView = adapter.getView(i, null, gridView)
            itemView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val alturaItem = itemView.measuredHeight
            alturaTotal += alturaItem
        }

        return alturaTotal
    }
}

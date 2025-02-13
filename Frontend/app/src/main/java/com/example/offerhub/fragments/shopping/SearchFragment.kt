package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import SearchViewModel
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieAnimationView
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentSearchBinding
import com.example.offerhub.interfaces.FilterData
import com.example.offerhub.interfaces.PromocionFragmentListener
import com.example.offerhub.util.ViewUtils
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search), FilterFragment.FilterListener, PromocionFragmentListener {
    private lateinit var binding: FragmentSearchBinding
    lateinit var viewModel: SearchViewModel
    private var badgeDrawable: BadgeDrawable? = null
    private val args by navArgs<SearchFragmentArgs>()
    private var scrollPosition: Int = 0
    private var useArg: Boolean = true
    private lateinit var adapter: PromocionGridAdapter

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    private fun initializeBadge() {
        if (badgeDrawable == null) {
            // Crea el badge y configúralo
            badgeDrawable =
                BadgeDrawable.createFromResource(requireContext(), R.xml.filter_badge).apply {
                    horizontalOffsetWithText = ViewUtils.dpToPx(resources, 20f).toInt()
                    verticalOffsetWithText = ViewUtils.dpToPx(resources, 20f).toInt()
                }
            binding.filterSearch.visibility = View.VISIBLE
            BadgeUtils.attachBadgeDrawable(badgeDrawable!!, binding.filterSearch)
            binding.filterSearch.post { binding.filterSearch.invalidate() }.also {
                if(binding.sergundaLinearLayoutBuscador.visibility == View.VISIBLE){
                    binding.filterSearch.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun calculateNumberOfFiltersApplied(): Int {
        // Agrega aquí la lógica para contar los filtros aplicados
        var num = 0
        if (viewModel.filtrosActuales != null) {
            val dias = if (viewModel.filtrosActuales!!.diasPromocion.size > 0) 1 else 0
            val tipoPromo = if (viewModel.filtrosActuales!!.tiposPromocion.size > 0) 1 else 0
            val vigencia =
                if (viewModel.filtrosActuales!!.desde.isNotEmpty() || viewModel.filtrosActuales!!.hasta.isNotEmpty()) 1 else 0
            num = dias + tipoPromo + vigencia
        }
        return num
    }

    private fun updateBadgeDrawable() {
        val number = calculateNumberOfFiltersApplied()
        if(badgeDrawable==null){
            initializeBadge()
        }
        Log.d("Filtros Aplicados",number.toString())
        when {
            number > 0 -> {
                badgeDrawable?.number = number
                badgeDrawable?.backgroundColor =
                    MaterialColors.getColor(
                        requireView(),
                        com.google.android.material.R.attr.colorError
                    )
            }

            else -> {
                badgeDrawable?.clearNumber()
                badgeDrawable?.backgroundColor =
                    ResourcesCompat.getColor(resources, android.R.color.transparent, null)
                badgeDrawable?.backgroundColor =
                    MaterialColors.getColor(
                        requireView(),
                        com.google.android.material.R.attr.colorError
                    )
            }
        }
        binding.filterSearch.invalidate()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get<SearchViewModel>(SearchViewModel::class.java)
        viewModel.filtrosActuales = FilterData("", "", mutableListOf(), mutableListOf())
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guarda los datos de búsqueda en el Bundle
        outState.putString("searchText", viewModel.textoBusqueda)
        outState.putParcelableArrayList("searchResults", ArrayList(viewModel.promociones))
        outState.putParcelable("currentFilters", viewModel.filtrosActuales)
    }


   override fun onResume() {
        super.onResume()

           if(viewModel.filtrosActuales!=null){
               onFiltersApplied(viewModel.filtrosActuales!!)
           }
       updateBadgeDrawable()
    }

    override fun onPause() {
        useArg = false
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }
    private fun showToast(message: String, duration: Long) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()

        // Oculta el mensaje después del tiempo especificado
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ toast.cancel() }, duration)
    }
    override fun mostrarAvisoSobreeleccion() {
        showToast("El limite de seleccion son 2 promociones.", 10000)
    }
    override fun updateButtonVisibility(shouldBeVisible: Boolean) {
        binding.btnCompararSearch.visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }

    override fun onFiltersApplied(filters: FilterData) {
        // Aplicar los filtros a las promociones en el ViewModel
        if(badgeDrawable==null){
            initializeBadge()
        }
        // 1. Obtener las promociones actuales desde el ViewModel (suponiendo que tengas una propiedad promociones en tu ViewModel)
        val promocionesActuales = viewModel.promociones
        // 2. Aplicar los filtros según corresponda
        val promocionesFiltradas = viewModel.filtrarPorVigencia(filters.desde, filters.hasta)
        val promocionesFiltradasPorTipo = viewModel.filtrarPorTipoPromocion(filters.tiposPromocion)
        val promocionesFiltradasPorDias = viewModel.filtrarPorDiasPromocion(filters.diasPromocion)
            viewModel.actualizarFiltrosActuales(filters)
        // 3. Combinar los resultados de los filtros (puedes ajustar esta lógica según tus necesidades)
        val promocionesResultantes = promocionesActuales
            .intersect(promocionesFiltradas)
            .intersect(promocionesFiltradasPorTipo)
            .intersect(promocionesFiltradasPorDias).toList()
        viewModel.promociones=promocionesActuales
        updateBadgeDrawable()
        // 4. Actualizar la lista de promociones en la interfaz de usuario (por ejemplo, en tu adaptador)
        adapter.vaciarCacheResultados()
        adapter.actualizarDatos(promocionesResultantes.sortedBy { it.titulo!!.lowercase() })
        adapter.notifyDataSetChanged()
    }


    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBadge()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    updateBadgeDrawable()
                }
            }
        }
        binding.filterSearch.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @androidx.annotation.OptIn(ExperimentalBadgeUtils::class)
            override fun onGlobalLayout() {
                badgeDrawable =
                    BadgeDrawable.createFromResource(requireContext(), R.xml.filter_badge).apply {
                        horizontalOffsetWithText = ViewUtils.dpToPx(resources, 20f).toInt()
                        verticalOffsetWithText = ViewUtils.dpToPx(resources, 20f).toInt()
                        BadgeUtils.attachBadgeDrawable(this, binding.filterSearch)
                    }
                updateBadgeDrawable()
                binding.filterSearch.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        var checkPrendido: Boolean = false
        adapter = PromocionGridAdapter(view.context, listOf(),this@SearchFragment)
        adapter.eliminarLista()
        adapter.setFragment(this@SearchFragment)
        val promocionesGridView =
            view.findViewById<GridView>(R.id.promocionesGridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        promocionesGridView.adapter = adapter
        binding.buscadores.setText("")
        val listView =
            view.findViewById<GridView>(R.id.gridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val contenedorCategorias =
            view.findViewById<LinearLayout>(R.id.sergundaLinearLayoutBuscador) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val sinPromociones =
            view.findViewById<TextView>(R.id.sinPromociones) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val promoGridView =
            view.findViewById<LinearLayout>(R.id.promoGridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        fun mostrarResultadosBusqueda() {
            contenedorCategorias.visibility = View.GONE
            promoGridView.visibility = View.VISIBLE
            binding.filterSearch.visibility = View.VISIBLE
            binding.comparador.visibility = View.VISIBLE
            binding.middleLine.visibility = View.VISIBLE
        }

        fun mostrarInicioBusqueda() {
            contenedorCategorias.visibility = View.VISIBLE
            promoGridView.visibility = View.GONE
            sinPromociones.visibility = View.GONE
            binding.buscadores.setText("")
            adapter.actualizarDatos(listOf())
            binding.filterSearch.visibility = View.GONE
            binding.comparador.visibility = View.GONE
            binding.middleLine.visibility = View.GONE
        }

        fun actualizarResultados(useFilters: Boolean =true) {
            try {
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBarResult)

                // Actualiza los datos en el adaptador existente
                if (viewModel.filtrosActuales != null && useFilters==true) {
                    onFiltersApplied(viewModel.filtrosActuales!!)
                }
                adapter.actualizarDatos(viewModel.promociones.sortedBy { it.titulo!!.lowercase() })
                // Notifica al GridView que los datos han cambiado
                adapter.vaciarCacheResultados()
                adapter.notifyDataSetChanged()
                promocionesGridView.adapter = adapter
                promoGridView.visibility = View.VISIBLE
                if (viewModel.textoBusqueda!=null && viewModel.textoBusqueda!!.length>0 && viewModel.promociones.size == 0) {
                     progressBar.visibility = View.GONE
                    sinPromociones.visibility = View.VISIBLE
                    promocionesGridView.visibility = View.GONE
                } else {
                    sinPromociones.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    promocionesGridView.visibility = View.VISIBLE
                }



            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

        coroutineScope.launch {
            // Actualiza la interfaz de usuario con los datos restaurados
            if (viewModel.filtrosActuales==null && viewModel.textoBusqueda != null && viewModel.textoBusqueda!!.isNotEmpty()) {
                binding.buscadores.setText(viewModel.textoBusqueda)
                actualizarResultados(false)
            }

        }.also {
            if (viewModel.textoBusqueda != null && viewModel.textoBusqueda!!.isNotEmpty()) {
                mostrarResultadosBusqueda()
            }

            if(viewModel.filtrosActuales!=null){
                binding.progressBarResult.visibility=View.GONE
            }
        }

        promocionesGridView.setOnItemClickListener { parent, _, position, _ ->
            val selectedPromo =
                adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador

            val action =
                SearchFragmentDirections.actionSearchFragmentToPromoDetailFragment(
                    selectedPromo
                )
            findNavController().navigate(action)
        }


        var datos: MutableList<String> = mutableListOf()
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                /*val datos: List<Comercio> =
                    instancia.leerBdClaseSinc("Comercio", "categoria", "Gastronomía")*/
                val datos = Funciones().obtenerCategorias(view.context)

                val adapterCat = CategoryGridAdapter(view.context, datos)
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedCategoria = adapterCat.getItem(position) as Categoria
                    if (selectedCategoria.nombre != null) {
                        binding.buscadores.setText(selectedCategoria.nombre)
                        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarResult)
                        progressBar.visibility=View.VISIBLE
                    }

                    viewModel.buscarPorCategoria(selectedCategoria.nombre).invokeOnCompletion {
                        actualizarResultados()
                    }.also {
                        mostrarResultadosBusqueda()
                    }
                }


                listView.adapter = adapterCat
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

// mi codigo de buscador


        if (args.categoria !== null && args.categoria != "" && useArg == true) {
            val nombreCategoria = args.categoria
            if (nombreCategoria.toString() != null) {
                binding.buscadores.setText(nombreCategoria.toString())
            }

            viewModel.buscarPorTexto(nombreCategoria.toString()).invokeOnCompletion {
                actualizarResultados()
            }.also {
                mostrarResultadosBusqueda()
            }
        }

        val lupa = view.findViewById<ImageView>(R.id.logoLupa)
        val cerrar = view.findViewById<ImageView>(R.id.logoCerrar)

        binding.logoLupa.setOnClickListener {
            val textoBusqueda = binding.buscadores.text.toString()
            if (textoBusqueda.length > 0) {

                viewModel.buscarPorTexto(textoBusqueda).invokeOnCompletion {
                    val progressBar = view.findViewById<ProgressBar>(R.id.progressBarResult)
                    progressBar.visibility=View.VISIBLE
                    actualizarResultados()
                }.also {
                    mostrarResultadosBusqueda()
                }
            }
        }

        binding.logoCerrar.setOnClickListener {
            mostrarInicioBusqueda()
            viewModel.textoBusqueda = ""
            viewModel.promociones = mutableListOf()
            viewModel.filtrosActuales = FilterData("", "", mutableListOf(), mutableListOf())
            onFiltersApplied(viewModel.filtrosActuales!!)
        }
        binding.botonGuardar.setOnClickListener{
            checkPrendido = !checkPrendido

            adapter.setCheckBoxesVisibility(checkPrendido)
            if (checkPrendido){

                val colorFondo = ContextCompat.getColor(requireContext(), R.color.g_gray500)
                val colorStateList = ColorStateList.valueOf(colorFondo)

// Establece el color de fondo en la vista.
                binding.botonGuardar.backgroundTintList = colorStateList
            }else{
                binding.btnCompararSearch.visibility=View.GONE
                val colorFondo = ContextCompat.getColor(requireContext(), R.color.white)
                val colorStateList = ColorStateList.valueOf(colorFondo)

// Establece el color de fondo en la vista.
                binding.botonGuardar.backgroundTintList = colorStateList
                adapter.eliminarLista()
                adapter.notifyDataSetChanged()
            }
        }

        binding.btnCompararSearch.setOnClickListener {
            var promos =adapter.getSeleccion()
            var promo1 = promos[0]
            var promo2 = promos[1]
            val bottomSheetDialog = CompararFragment.newInstance(promo1, promo2)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "CompararFragment")
        }
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

        binding.buscadores.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val textoBusqueda = binding.buscadores.text.toString()
                if (textoBusqueda.length > 0) {
                    val progressBar = view.findViewById<ProgressBar>(R.id.progressBarResult)
                    progressBar.visibility=View.VISIBLE
                    viewModel.buscarPorTexto(textoBusqueda).invokeOnCompletion {
                        actualizarResultados()
                    }.also {
                        mostrarResultadosBusqueda()
                    }
                }
            }
            hideKeyboard()
            false
        }

        binding.filterSearch.setOnClickListener {
            // Verifica si el FilterFragment ya está agregado
            val existingFilterFragment =
                parentFragmentManager.findFragmentByTag("FilterFragment") as? FilterFragment

            if (existingFilterFragment == null || !existingFilterFragment.isVisible) {
                // Si no se ha agregado o no es visible, crea uno nuevo y agrégalo
                val filterFragment = FilterFragment()

                // Resto de tu código para configurar el FilterFragment...

                // Asigna el SearchFragment como el oyente antes de agregar el FilterFragment
                filterFragment.filterListener = this@SearchFragment

                parentFragmentManager.beginTransaction()
                    .add(filterFragment, "FilterFragment")
                    .commit()
            }
        }
    }
}

package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import SearchViewModel
import android.content.Context
import android.os.Bundle
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
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.offerhub.util.ViewUtils
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search), FilterFragment.FilterListener {
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
            BadgeUtils.attachBadgeDrawable(badgeDrawable!!, binding.filterSearch)
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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get<SearchViewModel>(SearchViewModel::class.java)
        viewModel.filtrosActuales = FilterData("", "", mutableListOf(), mutableListOf())
        updateBadgeDrawable()
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

    override fun onFiltersApplied(filters: FilterData) {
        // Aplicar los filtros a las promociones en el ViewModel
        Log.d("SearchFragment", "OnFiltersApplied")
        // 1. Obtener las promociones actuales desde el ViewModel (suponiendo que tengas una propiedad promociones en tu ViewModel)
        val promocionesActuales = viewModel.promociones
        Log.d("Filtros que llegan - DESDE", filters.desde)
        Log.d("Filtros que llegan - HASTA", filters.hasta)
        Log.d("Filtros que llegan - TIPOS", filters.tiposPromocion.joinToString(","))
        Log.d("Filtros que llegan - DIAS", filters.diasPromocion.joinToString(","))
        // 2. Aplicar los filtros según corresponda
        val promocionesFiltradas = viewModel.filtrarPorVigencia(filters.desde, filters.hasta)
        val promocionesFiltradasPorTipo = viewModel.filtrarPorTipoPromocion(filters.tiposPromocion)
        val promocionesFiltradasPorDias = viewModel.filtrarPorDiasPromocion(filters.diasPromocion)

        // 3. Combinar los resultados de los filtros (puedes ajustar esta lógica según tus necesidades)
        val promocionesResultantes = promocionesActuales
            .intersect(promocionesFiltradas)
            .intersect(promocionesFiltradasPorTipo)
            .intersect(promocionesFiltradasPorDias).toList()
        viewModel.promociones=promocionesActuales
        updateBadgeDrawable()
        // 4. Actualizar la lista de promociones en la interfaz de usuario (por ejemplo, en tu adaptador)
        adapter.actualizarDatos(promocionesResultantes)
        adapter.notifyDataSetChanged()
    }


    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBadge()
        var instancia = InterfaceSinc()
        adapter = PromocionGridAdapter(view.context, listOf())
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
        }

        fun mostrarInicioBusqueda() {
            contenedorCategorias.visibility = View.VISIBLE
            promoGridView.visibility = View.GONE
            sinPromociones.visibility = View.GONE
            binding.buscadores.setText("")
            adapter.actualizarDatos(listOf())
            binding.filterSearch.visibility = View.GONE
        }

        fun actualizarResultados(useFilters: Boolean =true) {
            try {
                // Actualiza los datos en el adaptador existente
                if (viewModel.filtrosActuales != null && useFilters==true) {
                    onFiltersApplied(viewModel.filtrosActuales!!)
                }
                adapter.actualizarDatos(viewModel.promociones)
                // Notifica al GridView que los datos han cambiado
                adapter.notifyDataSetChanged()
                promocionesGridView.adapter = adapter
                promoGridView.visibility = View.VISIBLE
                if (viewModel.textoBusqueda!=null && viewModel.textoBusqueda!!.length>0 && viewModel.promociones.size == 0) {
                    sinPromociones.visibility = View.VISIBLE
                    promocionesGridView.visibility = View.GONE
                } else {
                    sinPromociones.visibility = View.GONE
                    promocionesGridView.visibility = View.VISIBLE
                }


            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }
        coroutineScope.launch {
            if (savedInstanceState != null) {
                viewModel.textoBusqueda = savedInstanceState.getString("searchText", "")
                val currentFilters =
                    savedInstanceState.getParcelable("currentFilters", FilterData::class.java)
                viewModel.filtrosActuales = currentFilters
                if (savedInstanceState != null) {
                    viewModel.promociones = savedInstanceState.getParcelableArrayList(
                        "searchResults",
                        Promocion::class.java
                    ) ?: mutableListOf()
                }
                updateBadgeDrawable()
            }
        }.invokeOnCompletion {
            // Actualiza la interfaz de usuario con los datos restaurados
            if (viewModel.textoBusqueda != null && viewModel.textoBusqueda!!.isNotEmpty()) {
                binding.buscadores.setText(viewModel.textoBusqueda)
                actualizarResultados(false)
            }

        }.also {
            if (viewModel.textoBusqueda != null && viewModel.textoBusqueda!!.isNotEmpty()) {
                mostrarResultadosBusqueda()
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

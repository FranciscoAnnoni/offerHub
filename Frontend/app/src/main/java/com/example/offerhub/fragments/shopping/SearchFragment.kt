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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentSearchBinding
import com.example.offerhub.viewmodel.ProfileViewModel
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    val viewModel by viewModels<SearchViewModel>()
    private val args by navArgs<SearchFragmentArgs>()
    private var scrollPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var instancia = InterfaceSinc()
        val adapter = PromocionGridAdapter(view.context, listOf())
        val promocionesGridView = view.findViewById<GridView>(R.id.promocionesGridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        promocionesGridView.adapter=adapter
        binding.buscadores.setText("")
        promocionesGridView.setOnItemClickListener { parent, _, position, _ ->
            val selectedPromo =
                adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador

            val action =
                SearchFragmentDirections.actionSearchFragmentToPromoDetailFragment(
                    selectedPromo
                )
            findNavController().navigate(action)
        }
        val listView = view.findViewById<GridView>(R.id.gridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val contenedorCategorias = view.findViewById<LinearLayout>(R.id.sergundaLinearLayoutBuscador) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val promoGridView = view.findViewById<LinearLayout>(R.id.promoGridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        fun mostrarResultadosBusqueda(){
            contenedorCategorias.visibility = View.GONE
            promoGridView.visibility = View.VISIBLE
        }
        fun mostrarInicioBusqueda(){
            contenedorCategorias.visibility = View.VISIBLE
            promoGridView.visibility = View.GONE
            binding.buscadores.setText("")
            adapter.actualizarDatos(listOf())
        }
        fun buscarPorTexto() {
            val textoBusqueda = binding.buscadores.text.toString()
            if(textoBusqueda.length>0) {
                mostrarResultadosBusqueda()
                CoroutineScope(Dispatchers.Main).launch {
                    val listaPromociones = LecturaBD().filtrarPromos(
                        listOf(
                            "titulo" to textoBusqueda,
                            "categoria" to textoBusqueda,
                        ), false
                    )
                    try {
                        // Actualiza los datos en el adaptador existente
                        adapter.actualizarDatos(listaPromociones)

                        // Notifica al GridView que los datos han cambiado
                        adapter.notifyDataSetChanged()
                        promocionesGridView.adapter = adapter
                        promoGridView.visibility = View.VISIBLE


                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }
            }
        }

        fun buscarPorCategoria(nombre:String){
            mostrarResultadosBusqueda()
            CoroutineScope(Dispatchers.Main).launch {
                val listaPromociones = LecturaBD().filtrarPromos(listOf(
                    "categoria" to nombre,
                ),false)
                try {
                    // Actualiza los datos en el adaptador existente
                    adapter.actualizarDatos(listaPromociones)
                    binding.buscadores.setText(nombre)
                    // Notifica al GridView que los datos han cambiado
                    adapter.notifyDataSetChanged()
                    promocionesGridView.adapter=adapter
                    promoGridView.visibility = View.VISIBLE


                } catch (e: Exception) {
                    println("Error al obtener promociones: ${e.message}")
                }
            }
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
                    buscarPorCategoria(selectedCategoria.nombre)
                }


                listView.adapter = adapterCat
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

// mi codigo de buscador


        if(args.categoria!==null && args.categoria!="") {
            val nombreCategoria = args.categoria
            buscarPorCategoria(nombreCategoria.toString())
        }

        val lupa = view.findViewById<ImageView>(R.id.logoLupa)
        val cerrar = view.findViewById<ImageView>(R.id.logoCerrar)

        binding.logoLupa.setOnClickListener{
            buscarPorTexto()
        }

        binding.logoCerrar.setOnClickListener{
            mostrarInicioBusqueda()
        }
        binding.buscadores.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verifica si el texto ha cambiado y muestra u oculta el logoClose
                if (s.isNullOrEmpty()) {
                    cerrar.visibility = View.GONE
                } else {
                    cerrar.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.buscadores.setOnEditorActionListener { _, actionId, event ->

            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                 buscarPorTexto()
                }
                hideKeyboard()
                //return@setOnEditorActionListener true
            false
        }


    }
}

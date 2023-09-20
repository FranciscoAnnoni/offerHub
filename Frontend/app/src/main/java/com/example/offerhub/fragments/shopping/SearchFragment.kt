package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import SearchViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentSearchBinding
import com.example.offerhub.viewmodel.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var instancia = InterfaceSinc()
        val listView = view.findViewById<GridView>(R.id.gridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<String> = mutableListOf()
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                /*val datos: List<Comercio> =
                    instancia.leerBdClaseSinc("Comercio", "categoria", "Gastronomía")*/
                val datos = Funciones().obtenerCategorias(view.context)
                val adapter = CategoryGridAdapter(view.context, datos)
                listView.adapter = adapter
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

// mi codigo de buscador

        val searchBar = view.findViewById<TextInputEditText>(R.id.buscadores)



        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s.toString()
                if (searchQuery.isNotEmpty()) {
                    // Realizar la búsqueda y actualizar la interfaz de usuario con los resultados
                    CoroutineScope(Dispatchers.Main).launch {
                        val results = viewModel.searchPromotions(searchQuery)
                        // Actualiza tu adaptador o vista con los resultados
                        //val adapter = CategoryGridAdapter(view.context, results)
                       // gridView.adapter = adapter
                    }
                } else {
                    // Limpiar la vista si la consulta está vacía

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })



    }
}
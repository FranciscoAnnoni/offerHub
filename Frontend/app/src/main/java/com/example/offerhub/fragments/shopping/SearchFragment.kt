package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding


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
                val datos = listOf(
                    Categoria(
                        view.context,
                        "Gastronomía",
                        "cat_gastronomia"
                    ),
                    Categoria(view.context, "Vehículos", "cat_vehiculos"),
                    Categoria(
                        view.context,
                        "Salud y Bienestar",
                        "cat_salud_y_bienestar"
                    ),
                    Categoria(view.context, "Hogar", "cat_hogar"),
                    Categoria(
                        view.context,
                        "Viajes y Turismo",
                        "cat_viajes"
                    ),
                    Categoria(
                        view.context,
                        "Entretenimiento",
                        "cat_entretenimiento"
                    ),
                    Categoria(
                        view.context,
                        "Indumentaria",
                        "cat_indumentaria"
                    ),
                    Categoria(
                        view.context,
                        "Supermercados",
                        "cat_supermercados"
                    ),
                    Categoria(
                        view.context,
                        "Electrónica",
                        "cat_electronica"
                    ),
                    Categoria(view.context, "Educación", "cat_educacion"),
                    Categoria(view.context, "Niños", "cat_ninos"),
                    Categoria(view.context, "Regalos", "cat_regalos"),
                    Categoria(view.context, "Bebidas", "cat_bebidas"),
                    Categoria(view.context, "Joyería", "cat_joyeria"),
                    Categoria(view.context, "Librerías", "cat_librerias"),
                    Categoria(view.context, "Mascotas", "cat_mascotas"),
                    Categoria(view.context, "Servicios", "cat_servicios"),
                    Categoria(view.context, "Otros", "cat_otros")
                    // Agrega más elementos según sea necesario
                )
                val adapter = CategoryGridAdapter(view.context, datos)
                listView.adapter = adapter
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

    }
}
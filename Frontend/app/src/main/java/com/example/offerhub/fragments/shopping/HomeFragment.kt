package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var instancia = InterfaceSinc()
        val listView = view.findViewById<GridView>(R.id.promocionesGridView) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<String> = mutableListOf()
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                val datos: List<Promocion> =
                    instancia.leerBdClaseSinc("Promocion", "categoria", "Gastronomía")
                val adapter = PromocionGridAdapter(view.context, datos)
                listView.adapter = adapter
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedPromo = adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                    val action = HomeFragmentDirections.actionHomeFragmentToPromoDetailFragment(selectedPromo)
                    findNavController().navigate(action)
                }
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }



    }
}
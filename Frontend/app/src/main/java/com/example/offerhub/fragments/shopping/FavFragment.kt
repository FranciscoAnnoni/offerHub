package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Usuario
import com.example.offerhub.databinding.FragmentFavBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class FavFragment: Fragment(R.layout.fragment_fav){

    private lateinit var binding: FragmentFavBinding
    private var scrollPosition: Int = 0


    override fun onPause() {
        super.onPause()
        scrollPosition = binding.gvFavoritos.scrollY
    }
    override fun onResume() {
        super.onResume()
        binding.gvFavoritos.post {
            binding.gvFavoritos.scrollTo(0, scrollPosition)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var funciones = Funciones()
        val listView = view.findViewById<GridView>(R.id.gvFavoritos) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val tvNoFavoritos = view.findViewById<TextView>(R.id.tvNoFavoritos)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<Promocion> = mutableListOf()


        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                val usuarioActual = (funciones.traerUsuarioActual() as Usuario?)!!
                val datos: List<Promocion> = funciones.obtenerPromocionesFavoritas(usuarioActual)

                if  (datos.isEmpty())  {
                    listView.visibility = View.GONE
                    tvNoFavoritos.visibility = View.VISIBLE
                }else {
                    listView.visibility = View.VISIBLE
                    tvNoFavoritos.visibility = View.GONE
                    val adapter = PromocionGridAdapter(view.context, datos)
                    listView.adapter = adapter

                    listView.setOnItemClickListener { parent, _, position, _ ->
                        val selectedPromo = adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                        val action = FavFragmentDirections.actionFavFragmentToPromoDetailFragment(selectedPromo)
                        findNavController().navigate(action)
                    }

                }

            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

    }
}
package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import UserViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.Globals
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
    private val userViewModel: UserViewModel by activityViewModels()
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
                val usuarioActual = Globals.asegurarUsuario()!!
                Log.d("Hola","1")

                coroutineScope.launch {
                    if(userViewModel.favoritos.isEmpty()){
                        userViewModel.favoritos = funciones.obtenerPromocionesFavoritas(usuarioActual)
                    }
                }.invokeOnCompletion {
                    Log.d("Hola","2")
                    if  (userViewModel.favoritos.isEmpty())  {
                        Log.d("Hola","3")
                        listView.visibility = View.GONE
                        tvNoFavoritos.visibility = View.VISIBLE
                    }else {
                        Log.d("Hola","4")
                        listView.visibility = View.VISIBLE
                        tvNoFavoritos.visibility = View.GONE
                        val adapter = PromocionGridAdapter(view.context, userViewModel.favoritos,userViewModel)
                        listView.adapter = adapter

                        listView.setOnItemClickListener { parent, _, position, _ ->
                            val selectedPromo = adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                            val action = FavFragmentDirections.actionFavFragmentToPromoDetailFragment(selectedPromo)
                            findNavController().navigate(action)
                        }

                    }
                }


            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

    }
}
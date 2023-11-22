package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import UserViewModel
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Usuario
import com.example.offerhub.databinding.FragmentFavBinding
import com.example.offerhub.interfaces.PromocionFragmentListener
import com.example.offerhub.viewmodel.UserViewModelSingleton

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class FavFragment: Fragment(R.layout.fragment_fav), PromocionFragmentListener {

    private lateinit var binding: FragmentFavBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavBinding.inflate(inflater)
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
       // binding.btnComparar.visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var funciones = Funciones()
        val listView = view.findViewById<GridView>(R.id.gvFavoritos) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val tvNoFavoritos = view.findViewById<TextView>(R.id.tvNoFavoritos)
        val tvNoFavoritos2 = view.findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.tvNoFavoritos2)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<Promocion> = mutableListOf()
        val userViewModel = UserViewModelSingleton.getUserViewModel()

        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                val usuarioActual = userViewModel.usuario!!
                coroutineScope.launch {
                    if(userViewModel.usuario==null){
                        userViewModel.favoritos = funciones.obtenerPromocionesFavoritas(usuarioActual)
                    }
                    if  (userViewModel.favoritos.size==0)  {
                        listView.visibility = View.GONE
                        tvNoFavoritos.visibility = View.VISIBLE
                        tvNoFavoritos2.visibility= View.VISIBLE
                    }else {
                        listView.visibility = View.VISIBLE
                        tvNoFavoritos.visibility = View.GONE
                        tvNoFavoritos2.visibility = View.GONE
                        val adapter = PromocionGridAdapter(view.context, userViewModel.favoritos,this@FavFragment)
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
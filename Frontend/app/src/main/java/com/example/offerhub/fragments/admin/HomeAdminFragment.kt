package com.example.offerhub.fragments.admin

import PromocionAdminGridAdapter
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.PromocionEscritura
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentHomeAdminBinding
import com.example.offerhub.databinding.FragmentHomePartnersBinding
import com.example.offerhub.fragments.shopping.CompararFragment
import com.example.offerhub.fragments.shopping.HomeFragmentDirections
import com.example.offerhub.funciones.FuncionesPartners
import com.example.offerhub.interfaces.PromocionFragmentListener
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeAdminFragment : Fragment(R.layout.fragment_home_admin), PromocionFragmentListener {
    private lateinit var binding: FragmentHomeAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userViewModel = UserViewModelSingleton.getUserViewModel()
        val promosContainer = view.findViewById<LinearLayout>(R.id.containerPromos)
        val listView = view.findViewById<GridView>(R.id.promocionesGridView)
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun cargarVista() {
                promosContainer.visibility=View.VISIBLE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()

                var checkPrendido: Boolean = false
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {
                    try {
                        var promocionesOrdenadas=userViewModel.listadoDePromosDisp.sortedBy { it.titulo!!.lowercase() }
                        val adapter = PromocionAdminGridAdapter(view.context, promocionesOrdenadas,this@HomeAdminFragment)
                        adapter.eliminarLista()
                        adapter.setFragment(this@HomeAdminFragment)
                        listView.adapter = adapter


                        listView.setOnItemClickListener { parent, _, position, _ ->
                            val selectedPromo =
                                adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                            val action =
                                HomeAdminFragmentDirections.actionHomeAdminFragmentToPromoDetailAdminFragment(
                                    selectedPromo
                                )
                            findNavController().navigate(action)
                        }
                        listView.visibility = View.VISIBLE

                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }
        }

        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            if(userViewModel.usuario==null){
                userViewModel.usuario = Funciones().traerUsuarioActual()
            }else {
                Log.d("Ya Existe usuario",userViewModel.usuario!!.nombre)
            }
            if(userViewModel.listadoDePromosDisp==null || userViewModel.listadoDePromosDisp.size==0){
                userViewModel.listadoDePromosDisp = FuncionesPartners().obtenerPromosPendientes()
                UserViewModelCache().guardarUserViewModel(userViewModel)
            }else {
                Log.d("YA Existen promos",userViewModel.listadoDePromosDisp.size.toString())
            }
            progressBar.visibility = View.GONE
        }.invokeOnCompletion {
            cargarVista()
        }


    }

    override fun mostrarAvisoSobreeleccion() {
        TODO("Not yet implemented")
    }

    override fun updateButtonVisibility(shouldBeVisible: Boolean) {
        TODO("Not yet implemented")
    }

}




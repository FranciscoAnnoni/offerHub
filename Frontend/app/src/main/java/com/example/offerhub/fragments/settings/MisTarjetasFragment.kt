package com.example.offerhub.fragments.settings

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
import com.example.offerhub.Usuario
import com.example.offerhub.activities.MisTarjetasAdapter
import com.example.offerhub.databinding.FragmentMisTarjetasBinding
import com.example.offerhub.util.hideBottomNavigationView
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MisTarjetasFragment: Fragment(){
    private lateinit var binding: FragmentMisTarjetasBinding
    private lateinit var tarjetasGridView: GridView
    private lateinit var usuario: Usuario
    private var uvm = UserViewModelSingleton.getUserViewModel()
    //private var gridViewAdapter: MisTarjetasAdapter = MisTarjetasAdapter(requireContext(), mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMisTarjetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uvm = UserViewModelSingleton.getUserViewModel()
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.llAgregarTarjeta.setOnClickListener {
            findNavController().navigate(R.id.action_misTarjetasFragment_to_cargadoTarjetasFragment)
        }


        var funciones = Funciones()
        var leerBD = LeerId()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        tarjetasGridView = view.findViewById<GridView>(R.id.gvMisTarjetas)
        //registerForContextMenu(tarjetasGridView)
        val job = coroutineScope.launch {

            usuario = UserViewModelSingleton.getUserViewModel().usuario!!
            var tarjetas: MutableList<Tarjeta> = mutableListOf()

            if (uvm.usuario!!.tarjetas != null ){
                for (tarjetaId in uvm.usuario!!.tarjetas!!) {

                    if (tarjetaId != null) {

                        val tarjetaObjeto = leerBD.obtenerTarjetaPorId(tarjetaId)
                        if (tarjetaObjeto != null) {
                            hideAnimacion()
                            tarjetas.add(tarjetaObjeto)

                        } else {

                            showAnimacion()

                            // ayuda para logear error
                        }
                    }
                }

                var gridViewAdapter = MisTarjetasAdapter(view.context, tarjetas)
                tarjetasGridView.adapter = gridViewAdapter

                if (gridViewAdapter.tarjetasUsuario.isEmpty()) {
                    showAnimacion()
                }


            }


        }


    }


    override fun onResume() {
        super.onResume()

        hideBottomNavigationView()
    }

    private fun hideAnimacion() {
        binding.apply {
            sinTarjetasAnimacion.visibility = View.GONE
            sinTarjetasTexto.visibility = View.GONE
        }
    }

    fun showAnimacion(){
        binding.apply {
            sinTarjetasAnimacion.visibility = View.VISIBLE
            sinTarjetasTexto.visibility = View.VISIBLE
        }
    }

   /* override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.eliminar_tarjeta_menu, menu)
    }*/

    /*override fun onContextItemSelected(item: MenuItem): Boolean {
        uvm = UserViewModelSingleton.getUserViewModel()
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val tarjeta = tarjetasGridView.adapter.getItem(position) as Tarjeta
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var funciones = Funciones()

        if(item.itemId == R.id.opcionEliminarTarjeta) {
            coroutineScope.launch {
                funciones.eliminarTodasLasTarjetasDeUsuario(usuario.id)
                uvm.usuario!!.tarjetas!!.remove(tarjeta.id)
                if (uvm.usuario!!.tarjetas != null) {
                      if(uvm.usuario!!.tarjetas!!.isNotEmpty()) {
                          funciones.agregarTarjetasAUsuario(
                              usuario.id,
                              uvm.usuario!!.tarjetas!! as MutableList<String>
                          )
                      }
                    }
                UserViewModelCache().guardarUserViewModel(uvm)
                uvm.listadoDePromosDisp=uvm.listadoDePromosDisp.filterNot { promo ->
                    (promo.tarjetas?.contains(tarjeta.id) == true)
                }

                UserViewModelCache().guardarUserViewModel(uvm)
            }

            (tarjetasGridView.adapter as MisTarjetasAdapter).removeTarjeta(tarjeta)

            // Notifica al adaptador que los datos han cambiado
            (tarjetasGridView.adapter as MisTarjetasAdapter).notifyDataSetChanged()

            Toast.makeText(requireContext(), "Tarjeta Eliminada", Toast.LENGTH_LONG).show()

            return true
        }
        return true
    }*/


}
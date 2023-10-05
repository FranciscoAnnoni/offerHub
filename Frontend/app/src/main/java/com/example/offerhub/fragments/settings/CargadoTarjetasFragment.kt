package com.example.offerhub.fragments.settings

import TarjetasListViewAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
//import com.example.offerhub.activities.EntidadTarjetasAdapter
//import com.example.offerhub.databinding.FragmentEntidadTarjetasBinding
import com.example.offerhub.databinding.FragmentTarjetasBinding
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
//import com.example.offerhub.viewmodel.CargadoTarjetasViewModel
import dagger.hilt.internal.aggregatedroot.codegen._com_example_offerhub_KelineApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CargadoTarjetasFragment : Fragment() {
    private lateinit var binding: FragmentTarjetasBinding
    //private val viewModel by viewModels<CargadoTarjetasViewModel>()
    var entidadesExpanded: MutableList<Boolean> = mutableListOf(false,false,false,false,false,false)
    var tarjetasSeleccionadas: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTarjetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var instancia = InterfaceSinc()
        var funciones = Funciones()
        val entidadesContainer = view.findViewById<LinearLayout>(R.id.llEntidadesContainer)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val scrollView = view.findViewById<ScrollView>(R.id.svOpcionesTarjetas)

        binding.imageClose.setOnClickListener{
            findNavController().navigateUp()
        }

        var datos: MutableList<String> = mutableListOf()
        val job = coroutineScope.launch {
        var uvm = UserViewModelSingleton.getUserViewModel()
        var usuario = uvm.usuario!!
        //var tarjetasUsuario = usuario.tarjetas!!

        binding.botonGuardar.setOnClickListener {
            Log.d("Tarjetas a Guardar",tarjetasSeleccionadas.joinToString(","))
            if (uvm.usuario!!.tarjetas == null ) {
                uvm.usuario!!.tarjetas = tarjetasSeleccionadas as MutableList<String?>
            } else {
                uvm.usuario!!.tarjetas!!.addAll(tarjetasSeleccionadas)
                Log.d("Despues de agregar tarjetas, tarjetas uvm: ", uvm.usuario!!.tarjetas!!.joinToString(","))
            }
            UserViewModelCache().guardarUserViewModel(uvm)
            funciones.agregarTarjetasAUsuario( usuario.id, tarjetasSeleccionadas)
            tarjetasSeleccionadas = mutableListOf()

            CoroutineScope(Dispatchers.Main).launch{
                uvm.listadoDePromosDisp = funciones.obtenerPromociones(uvm.usuario!!)
                UserViewModelCache().guardarUserViewModel(uvm)
            }

            Toast.makeText(view.context, "Los cambios han sido guardados", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()

        }

            scrollView.visibility = View.VISIBLE
            entidadesContainer.visibility = View.GONE
            val entidades = funciones.traerEntidades()

            for ((index, entidad) in entidades.withIndex()) {

                var listaTarjetas: MutableList<Tarjeta> = mutableListOf()
                try {
                    Log.d("UVM TARJETAS DISP: ", uvm.tarjetasDisponibles.joinToString(","))
                    //listaTarjetas = uvm.tarjetasDisponibles.filter { tarjeta -> tarjeta.entidad  == entidad.id } as MutableList<Tarjeta>
                    Log.d("UVM TARJETAS DISP: ", uvm.tarjetasDisponibles.joinToString(","))
                    listaTarjetas = instancia.leerBdClaseSinc("Tarjeta", "entidad", entidad.id!!)
                    for (data in listaTarjetas){
                        Log.d("Promocion", "titulo: $data")
                    }
                }
                catch (e: Exception) {
                    println("Error al obtener promociones: ${e.message}")
                }

                if (listaTarjetas.size >0) {

                    val entidadLl = LinearLayout(view.context)
                    entidadLl.orientation = LinearLayout.HORIZONTAL


                    val layoutParams = entidadLl.layoutParams as? LinearLayout.LayoutParams ?: LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.espacio_entre_titulos) // Ajusta este valor según tus necesidades
                    entidadLl.layoutParams = layoutParams

                    val entidadTitle = TextView(requireContext())
                    entidadTitle.text = entidad.nombre
                    entidadTitle.textSize = 20f
                    entidadTitle.width = 750
                    entidadTitle.setPadding(0, 16, 20, 8)

                    val imagen = ImageView(requireContext())
                    imagen.setImageResource(R.drawable.ic_expand_more)
                    imagen.setPadding(20, 10, 0, 0)

                    entidadLl.addView(entidadTitle)
                    entidadLl.addView(imagen)

                    val listView = ListView(requireContext())
                    //listView.setPadding(10, 10, 10, 60)
                    val tarjetasUsuario = uvm.usuario!!.tarjetas
                    var listaSinRepetidos = mutableListOf<Tarjeta>()
                    if (tarjetasUsuario != null) {
                         for(tarjeta in listaTarjetas) {
                             if (!tarjetasUsuario.contains(tarjeta.id)) {
                                 listaSinRepetidos.add(tarjeta)
                             }
                         }
                    } else {
                        listaSinRepetidos = listaTarjetas
                    }
                    val arrayAdapter = TarjetasListViewAdapter(requireContext(), listaSinRepetidos)
                    listView.adapter = arrayAdapter
                    listView.setOnItemClickListener { parent, view, position, id ->

                        var tarjeta= parent.getItemAtPosition(position) as Tarjeta

                        val checkbox = view.findViewById<ImageView>(R.id.ivOpcionTarjeta)
                        if (tarjetasSeleccionadas.contains(tarjeta.id)) {
                            checkbox.setImageResource(R.drawable.empty_checkbox)
                            tarjetasSeleccionadas.remove(tarjeta.id)
                        } else {
                            checkbox.setImageResource(R.drawable.full_checkbox)
                            tarjetasSeleccionadas.add(tarjeta.id!!)
                        }

                    }
                    listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                    listView.visibility = View.GONE



                    entidadesContainer.addView(entidadLl)
                    entidadesContainer.addView(listView)

                    entidadLl.setOnClickListener {
                        entidadesExpanded[index] = !entidadesExpanded[index]
                        if(entidadesExpanded[index]) {
                            val layoutParams = entidadLl.layoutParams as? LinearLayout.LayoutParams ?: LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.espacio_entre_titulos_desplegados) // Ajusta este valor según tus necesidades
                            entidadLl.layoutParams = layoutParams
                            listView.visibility = View.VISIBLE
                            imagen.setImageResource(R.drawable.ic_expand_less)

                        } else {
                            val layoutParams = entidadLl.layoutParams as? LinearLayout.LayoutParams ?: LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.espacio_entre_titulos) // Ajusta este valor según tus necesidades
                            entidadLl.layoutParams = layoutParams
                            listView.visibility = View.GONE
                            imagen.setImageResource(R.drawable.ic_expand_more)

                        }
                    }


                }
            }
        entidadesContainer.visibility = View.VISIBLE
        }

    }

}


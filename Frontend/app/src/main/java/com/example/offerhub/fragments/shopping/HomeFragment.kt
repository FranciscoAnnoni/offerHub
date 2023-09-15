package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import PromocionGridPorCategoriaAdapter
import UserViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria
import com.example.offerhub.databinding.FragmentHomeBinding
import com.example.offerhub.funciones.getFavResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.ceil


class HomeFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentHomeBinding
    private var scrollPosition: Int = 0
    var isFavorite = false
    var listenerHabilitado = false
    private val userViewModel: UserViewModel by activityViewModels()

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
        var funciones = Funciones()
        val categoriasContainer = view.findViewById<LinearLayout>(R.id.categoriasContainer)
        val promosContainer = view.findViewById<LinearLayout>(R.id.containerPromos)
        val homeScrollView = view.findViewById<ScrollView>(R.id.homeScrollView)
        val listView = view.findViewById<GridView>(R.id.promocionesGridView)
        val mySwitch = view.findViewById<Switch>(R.id.switchHomeMode)
        val coroutineScope = CoroutineScope(Dispatchers.Main)



        fun cargarVista() {
            if (userViewModel.homeModoFull=="1") {
                val promoFav = view.findViewById<ImageView>(R.id.promoFav)
                promosContainer.visibility=View.VISIBLE
                homeScrollView.visibility = View.GONE
                listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {
                    try {
                        val adapter = PromocionGridAdapter(view.context, userViewModel.listadoDePromosDisp)
                        listView.adapter = adapter
                        listView.setOnItemClickListener { parent, _, position, _ ->
                            val selectedPromo =
                                adapter.getItem(position) as Promocion // Reemplaza "adapter" con el nombre de tu adaptador
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToPromoDetailFragment(
                                    selectedPromo
                                )
                            findNavController().navigate(action)
                        }
                        listView.visibility = View.VISIBLE

                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }
            } else {
                //val listView = view.findViewById<GridView>(R.id.promocionesGridView)
                //  val promoFav = view.findViewById<ImageView>(R.id.promoFav)

                // listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {

                    // Seteo Visibilidad
                    homeScrollView.visibility = View.VISIBLE
                    categoriasContainer.visibility = View.GONE
                    promosContainer.visibility = View.GONE
                    categoriasContainer.visibility = View.GONE
                    //Obtengo el listado de categorias.
                    val categorias = funciones.obtenerCategorias(view.context)
                    for (categoria in categorias) {
                        // Crea un título de categoría
                        val promociones: List<Promocion> =
                            userViewModel.listadoDePromosDisp.filter { it.categoria == categoria.nombre }.take(11)
                        if (promociones.size > 0) {
                            val categoriaTitle = TextView(requireContext())
                            categoriaTitle.text = categoria.nombre
                            categoriaTitle.textSize = 20f
                            categoriaTitle.setPadding(0, 16, 0, 8)

                            // Crea un RecyclerView para esta categoría
                            val recyclerView = RecyclerView(requireContext())
                            recyclerView.layoutManager = LinearLayoutManager(
                                requireContext(),
                                RecyclerView.HORIZONTAL,
                                false
                            )

                            // Obtén las promociones para esta categoría (reemplaza esto con tu lógica real)


                            // Configura el adaptador para el RecyclerView
                            val adapter = PromocionGridPorCategoriaAdapter(
                                requireContext(),
                                promociones,
                                object : PromocionGridPorCategoriaAdapter.OnItemClickListener {
                                    override fun onItemClick(promocion: Promocion) {
                                        // Maneja el clic en el elemento aquí
                                        val action =
                                            HomeFragmentDirections.actionHomeFragmentToPromoDetailFragment(
                                                promocion
                                            )
                                        findNavController().navigate(action)
                                    }
                                })
                            recyclerView.adapter = adapter

                            // Agrega el título y el RecyclerView al contenedor
                            categoriasContainer.addView(categoriaTitle)
                            categoriasContainer.addView(recyclerView)

                        }
                    }
                    categoriasContainer.visibility = View.VISIBLE
                }
            }
        }
        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            if (userViewModel.listadoDePromosDisp.isEmpty()) {
                // Si no, obtén las promociones y guárdalas en el ViewModel
                userViewModel.listadoDePromosDisp = Funciones().obtenerPromociones(Funciones().traerUsuarioActual())
            }
            Log.d("homeModoFull",userViewModel.homeModoFull.toString())
            if(userViewModel.homeModoFull==null){
                userViewModel.homeModoFull=Funciones().traerUsuarioActual()?.homeModoFull
            }
            if(userViewModel.id==null){
                userViewModel.id=Funciones().traerUsuarioActual()?.id
            }
            Log.d("homeModoFull",userViewModel.homeModoFull.toString())
            listenerHabilitado=false
            mySwitch.isChecked= userViewModel.homeModoFull=="1"
            listenerHabilitado=true
            progressBar.visibility = View.GONE
        }.invokeOnCompletion {
            cargarVista()
        }
        mySwitch.setOnClickListener {
            if(listenerHabilitado){

            val coroutineScope = CoroutineScope(Dispatchers.Main)

            // Llamar a la función que obtiene los datos.
            val job = coroutineScope.launch {
                userViewModel.homeModoFull=if(userViewModel.homeModoFull=="1") "0" else "1"
                    EscribirBD().editarAtributoDeClase("Usuario",
                        userViewModel.id.toString(),"homeModoFull",userViewModel.homeModoFull.toString())
                cargarVista()
            }
        }
        }

    }

    fun calcularAlturaTotalGridView(gridView: GridView): Int {
        val adapter = gridView.adapter
        var alturaTotal = 0

        for (i in 0 until ceil((adapter.count/2).toDouble()).toInt()) {
            val itemView = adapter.getView(i, null, gridView)
            itemView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val alturaItem = itemView.measuredHeight
            alturaTotal += alturaItem
        }

        return alturaTotal
    }
}
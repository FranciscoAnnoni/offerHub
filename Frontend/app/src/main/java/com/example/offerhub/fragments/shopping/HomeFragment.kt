package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import PromocionGridPorCategoriaAdapter
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
    var modoVerTodas =false
    var listenerHabilitado = true

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
            if (modoVerTodas) {
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar2)

                val promoFav = view.findViewById<ImageView>(R.id.promoFav)
                progressBar.visibility = View.VISIBLE
                promosContainer.visibility=View.VISIBLE
                homeScrollView.visibility = View.GONE
                listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {
                    try {
                        val datos: List<Promocion> =
                            funciones.obtenerPromociones(funciones.traerUsuarioActual())
                        val adapter = PromocionGridAdapter(view.context, datos)
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
                        progressBar.visibility = View.GONE

                    } catch (e: Exception) {
                        println("Error al obtener promociones: ${e.message}")
                    }
                }
            } else {
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                //val listView = view.findViewById<GridView>(R.id.promocionesGridView)
                //  val promoFav = view.findViewById<ImageView>(R.id.promoFav)

                // listView.visibility = View.GONE
                val coroutineScope = CoroutineScope(Dispatchers.Main)
                var datos: MutableList<String> = mutableListOf()
                // Llamar a la función que obtiene los datos.
                val job = coroutineScope.launch {


                    homeScrollView.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    categoriasContainer.visibility = View.GONE
                    promosContainer.visibility = View.GONE
                    categoriasContainer.visibility = View.GONE
                    val categorias = listOf(
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
                    )
                    val todasPromociones: List<Promocion> =
                        Funciones().obtenerPromociones(Funciones().traerUsuarioActual())
                    for (categoria in categorias) {
                        // Crea un título de categoría
                        val promociones: List<Promocion> =
                            todasPromociones.filter { it.categoria == categoria.nombre }.take(11)
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
                    progressBar.visibility = View.GONE
                    categoriasContainer.visibility = View.VISIBLE
                }
            }
        }
        val job = coroutineScope.launch {
            modoVerTodas= Funciones().traerUsuarioActual()?.homeModoFull == "1"
            Funciones().traerUsuarioActual()?.homeModoFull?.let { Log.d("modoFull", it) }
listenerHabilitado=false
            mySwitch.isChecked=modoVerTodas
            listenerHabilitado=true
            cargarVista()
        }
        mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(listenerHabilitado){
                modoVerTodas=!modoVerTodas
            val coroutineScope = CoroutineScope(Dispatchers.Main)

            // Llamar a la función que obtiene los datos.
            val job = coroutineScope.launch {
                Funciones().traerUsuarioActual()?.let {
                    EscribirBD().editarAtributoDeClase("Usuario",
                        it.id,"homeModoFull",if(modoVerTodas) "1" else "0")
                }
            }
            cargarVista()
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
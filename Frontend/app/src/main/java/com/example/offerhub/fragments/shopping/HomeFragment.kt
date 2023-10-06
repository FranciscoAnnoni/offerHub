package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import android.os.Handler
import android.os.Looper
import PromocionGridPorCategoriaAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.EscribirBD
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentHomeBinding
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.ceil


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private var checkPrendido: Boolean = false
    private var scrollPosition: Int = 0
    var isFavorite = false
    var listenerHabilitado = false
    private fun showToast(message: String, duration: Long) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()

        // Oculta el mensaje después del tiempo especificado
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ toast.cancel() }, duration)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }
    fun updateButtonVisibility(shouldBeVisible: Boolean) {
        binding.btnComparar.visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
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
        val userViewModel = UserViewModelSingleton.getUserViewModel()

        fun cargarVista() {
            if (userViewModel.usuario!!.homeModoFull=="1") {
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
                        adapter.setHomeFragment(this@HomeFragment)
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
                        binding.botonGuardar.setOnClickListener{
                            checkPrendido = !checkPrendido

                            adapter.setCheckBoxesVisibility(checkPrendido)
                            if (checkPrendido){
                                showToast("Para poder comparar debe seleccionar 2 promociones.", 10000)
                            }
                        }

                        binding.btnComparar.setOnClickListener {
                            var promos =adapter.getSeleccion()
                            var promo1 = promos[0] as Promocion
                            var promo2 = promos[1] as Promocion
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToCompararFragment(
                                    promo1,promo2
                                )
                            findNavController().navigate(action)
                        }

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
                            if (requireContext() != null) {
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
                                    activity!!,
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

                                        override fun onVerMasClick() {
                                            TODO("Not yet implemented")
                                        }
                                    },
                                    object : PromocionGridPorCategoriaAdapter.OnItemClickListener {
                                        override fun onItemClick(promocion: Promocion) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onVerMasClick() {
                                            //findNavController().navigate(R.id.favFragment)
                                           var navController=requireActivity().findNavController(R.id.mainAppFragment)
                                            navController.popBackStack(R.id.homeFragment, false);
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                                                    categoria.nombre
                                                )
                                            navController.navigate(action);
                                            // Maneja el clic en el elemento aquí
                                            /*
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                                                    categoria.nombre
                                                )
                                            findNavController().navigate(action)

                                             */
                                        }

                                    })
                                recyclerView.adapter = adapter

                                // Agrega el título y el RecyclerView al contenedor
                                categoriasContainer.addView(categoriaTitle)
                                categoriasContainer.addView(recyclerView)

                            }
                        }
                    }
                    categoriasContainer.visibility = View.VISIBLE
                }
            }
        }
        coroutineScope.launch {
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE

            if(userViewModel.usuario==null){
                userViewModel.usuario = Funciones().traerUsuarioActual()
            }else {
                Log.d("YA Existe usuario 123221",userViewModel.usuario!!.nombre)
            }
            Log.d("Logueandome",userViewModel.usuario!!.nombre)
            Log.d("Logueandome",userViewModel.listadoDePromosDisp.count().toString())
            Log.d("Logueandome",userViewModel.favoritos.count().toString())
            listenerHabilitado=false
            mySwitch.isChecked= userViewModel.usuario!!.homeModoFull=="1"
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
                userViewModel.usuario!!.homeModoFull=if(userViewModel.usuario!!.homeModoFull=="1") "0" else "1"
                UserViewModelCache().guardarUserViewModel(userViewModel)
                EscribirBD().editarAtributoDeClase("Usuario",
                    userViewModel.usuario!!.id.toString(),"homeModoFull",userViewModel.usuario!!.homeModoFull.toString())
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

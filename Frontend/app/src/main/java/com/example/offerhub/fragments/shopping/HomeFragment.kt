package com.example.offerhub.fragments.shopping

import CategoryGridAdapter
import PromocionGridAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
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

    override fun onPause() {
        super.onPause()
        scrollPosition = binding.promocionesGridView.scrollY
    }
    override fun onResume() {
        super.onResume()
        binding.promocionesGridView.post {
            binding.promocionesGridView.scrollTo(0, scrollPosition)
        }
    }

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
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val listView = view.findViewById<GridView>(R.id.promocionesGridView)
        val promoFav = view.findViewById<ImageView>(R.id.promoFav)
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
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
//                promoFav.setOnClickListener {  view->
//                    isFavorite = !isFavorite // Cambiar el estado al contrario
//                    val selectedPromoId = view.getTag(R.id.promocion_id) as String
//                    // Cambiar la imagen según el estado
//                    var favoriteImageResource =R.drawable.ic_fav
//                    if (isFavorite) {
//                        coroutineScope.launch {
//                            funciones.agregarPromocionAFavoritos(
//                                funciones.traerUsuarioActual()?.id.toString(),
//                                selectedPromoId
//                            )
//                        }
//                    } else {
//                        coroutineScope.launch {
//                            funciones.elimiarPromocionDeFavoritos(
//                                funciones.traerUsuarioActual()?.id.toString(),
//                                selectedPromoId
//                            )
//                        }
//                    }
//
//                    // Establecer la imagen en la ImageView
//                    promoFav.setImageResource(getFavResource(isFavorite))
//                }
                val alturaTotal = resources.getDimensionPixelSize(R.dimen.altura_grid_view)
                val params = listView.layoutParams
                params.height = alturaTotal
                listView.layoutParams = params
                listView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
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
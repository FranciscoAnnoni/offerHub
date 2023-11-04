package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar

import androidx.navigation.fragment.findNavController
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentHomePartnersBinding
import com.example.offerhub.funciones.FuncionesPartners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePartnersFragment : Fragment(R.layout.fragment_home_partners) {
    private lateinit var binding: FragmentHomePartnersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePartnersBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var promociones = emptyList<Promocion>()
        var promosDisponibles = emptyList<Promocion>()
        var promocionesRechazadas = emptyList<Promocion>()
        val promocionesRechazadasHeader = view.findViewById<TextView>(R.id.promocionesRechazadasHeader)
        val promocionesRechazadasLayout = view.findViewById<LinearLayout>(R.id.promocionesRechazadasLayout)
        val promocionesHeader = view.findViewById<TextView>(R.id.promotionsHeader)
        val promocionesLayout = view.findViewById<LinearLayout>(R.id.promotionsLayout)
        var botonAgregarPromocion = view.findViewById<LinearLayout>(R.id.llAgregarPromocion)
        var disponiblesProgress = view.findViewById<ProgressBar>(R.id.disponiblesProgress)
        var rechazadasProgress = view.findViewById<ProgressBar>(R.id.rechazadasProgress)



        val job = CoroutineScope(Dispatchers.Main).launch {

            val usuario = Funciones().traerUsuarioPartner()
            if (usuario != null) {
                view.findViewById<TextView>(R.id.inicioHome).text = "Bienvenido " + usuario.nombreDeEmpresa
                if (usuario.idComercio != null) {
                    promociones = FuncionesPartners().obtenerPromosPorComercio(
                        usuario.idComercio!!
                    )
                }
            }
            disponiblesProgress.visibility=View.VISIBLE
            rechazadasProgress.visibility=View.VISIBLE
            promocionesRechazadas = promociones.filter { it.estado!!.lowercase() == "rechazado" }
            promosDisponibles = promociones.filterNot { it in promocionesRechazadas }
        }.invokeOnCompletion {
            cargarPromociones(promosDisponibles,promocionesRechazadas)
        }
        botonAgregarPromocion.setOnClickListener {
            findNavController().navigate(R.id.action_homePartnersFragment_to_cargarPromocionPartnersFragment)
        }
            promocionesHeader.setOnClickListener {
                if (promocionesLayout.visibility == View.VISIBLE) {
                    promocionesLayout.visibility = View.GONE
                    promocionesHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_expand_more, 0
                    )
                } else {
                    promocionesLayout.visibility = View.VISIBLE
                    promocionesHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_expand_less, 0
                    )

                }
            }

            promocionesRechazadasHeader.setOnClickListener {
                if (promocionesRechazadasLayout.visibility == View.VISIBLE) {
                    promocionesRechazadasLayout.visibility = View.GONE
                    promocionesRechazadasHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_expand_more, 0
                    )
                } else {
                    promocionesRechazadasLayout.visibility = View.VISIBLE
                    promocionesRechazadasHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.ic_expand_less, 0
                    )
                }
            }
        }
    fun editPromocion(promo:Promocion){
        val bundle = Bundle()
        bundle.putParcelable("promocion", promo)
        bundle.putBoolean("isEditing", true)

        // Navegar usando NavController
        findNavController().navigate(R.id.action_homePartnersFragment_to_cargarPromocionPartnersFragment, bundle)
    }

    fun cargarPromociones(promosDisponibles:List<Promocion>,promocionesRechazadas:List<Promocion>){
        if (promosDisponibles.isNotEmpty()) {
            val recyclerView =
                view!!.findViewById<RecyclerView>(R.id.promotionsRecyclerView)
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            recyclerView.adapter = PromotionsAdapterPartners(this@HomePartnersFragment,promosDisponibles as MutableList<Promocion>,true,this)
        }
        if (promocionesRechazadas.isNotEmpty()) {
            val recyclerView =
                view!!.findViewById<RecyclerView>(R.id.promotionsRechazadasRecyclerView)
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            recyclerView.adapter = PromotionsAdapterPartners(this@HomePartnersFragment,promocionesRechazadas as MutableList<Promocion>,false,this)
        }
        view!!.findViewById<ProgressBar>(R.id.disponiblesProgress).visibility=View.GONE
        view!!.findViewById<ProgressBar>(R.id.rechazadasProgress).visibility=View.GONE
    }
}




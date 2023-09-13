package com.example.offerhub.fragments.shopping

import PromocionGridAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.offerhub.Funciones
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
        var instancia = Funciones()
        val listView = view.findViewById<GridView>(R.id.gvFavoritos) // Reemplaza "listView" con el ID de tu ListView en el XML.
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var datos: MutableList<Promocion> = mutableListOf()
        var tarjetas: List<String?> = listOf("-NcDHYyW9d0QE9gyP8Nt", "-NcDH_GMqKIPLS45m4uA", "-NcDHaymq_ZTES7qQi8z")
        var favoritos: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHaAMYXz2y6-VrOol", "-NcDHbt8duv0PY2Mg-HS")
        var wishlistComercio: List<String?> = listOf("-NcDHYhXsoVxe4Hr_Qtj", "-NcDHahG-cL1CBcg3amc", "-NcDHcR075g8wtxSJQ46")
        var wishlistRubro: List<String?> = listOf("Supermercados")
        var reintegro: List<String?> = listOf("-NcDH_0240n4Qg2x_GN1", "-NcDHbt8duv0PY2Mg-HS")
        var usuario = Usuario("1","Adam Bareiro", "adam9@gmail.com", tarjetas, favoritos, wishlistComercio, wishlistRubro, reintegro)
        var promocionMock = Promocion(
            "123456",
            "Gastronomía",
            "1810",
            "",
            listOf("Lunes", "Martes", "Miercoles"),
            "30",
            "",
            listOf("Guatemala"),
            listOf("Santander"),
            "descuento",
            "30% de Descuento en Efectivo",
            "",
            "",
            "",
            "",
            "",
            org.threeten.bp.LocalDate.now(),
            org.threeten.bp.LocalDate.now(),
            "",
            ""
        )

        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            try {
                val datos: List<Promocion> = listOf(promocionMock, promocionMock, promocionMock, promocionMock, promocionMock, promocionMock)
                val adapter = PromocionGridAdapter(view.context, datos)
                listView.adapter = adapter
            } catch (e: Exception) {
                println("Error al obtener promociones: ${e.message}")
            }
        }

    }
}
package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.activities.ListaSucursalAdapter
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentPartnersSucursalesBinding
import com.example.offerhub.interfaces.OnAddItemListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartnersSucursalesFragment: Fragment(), OnAddItemListener {
    private lateinit var binding: FragmentPartnersSucursalesBinding
    private val sucursales = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPartnersSucursalesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var usuario: UserPartner?
        var idComercio = ""
        var comercio = Comercio()



        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            usuario = Funciones().traerUsuarioPartner()

            if (usuario != null) {
                Log.d("id usuario sucursales", usuario!!.id)
                if (usuario!!.idComercio != null) {
                    idComercio = usuario!!.idComercio!!
                    Log.d("id comercio", idComercio)
                    val lecturaComercio = LeerId().obtenerComercioPorId(idComercio)
                    if (lecturaComercio != null) {
                        Log.d("El comercio no es null, nombre comercio: ", lecturaComercio!!.nombre!!)
                        comercio = lecturaComercio!!
                    }
                }
            }
            if (comercio.sucursales != null && comercio.sucursales!!.isNotEmpty()) {
                Log.d("Las sucursales no estan vacias: ", comercio.sucursales!!.joinToString(","))
                for (sucursal in comercio.sucursales!!) {
                    if (sucursal != null) {
                        sucursales.add(sucursal)
                    }
                }
            }

            Log.d("Sucursales luego del for: ", sucursales.joinToString(","))

            var sucursalesAdapter = ListaSucursalAdapter(requireContext(), sucursales)
            binding.lvSucursales.adapter = sucursalesAdapter


        }

        binding.botonAgregarSucursal.setOnClickListener {
            val bottomSheetDialog = AgregarSucursalFragment.newInstance(comercio,this)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "AgregarSucursal")
        }



    }

    override fun onAddItem(item: String) {
        updateSucursalesList(item)

    }

    fun updateSucursalesList(newItem: String) {
        sucursales.add(newItem)
        (binding.lvSucursales.adapter as ListaSucursalAdapter).notifyDataSetChanged()
    }

}
package com.example.offerhub.fragments.partners

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.LecturaBD
import com.example.offerhub.LeerId
import com.example.offerhub.PromocionEscritura
import com.example.offerhub.R
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentCargarPromocionPartnersBinding
import com.example.offerhub.fragments.shopping.FilterFragment
import com.example.offerhub.funciones.FuncionesPartners
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


class CargarPromocionPartnersFragment: Fragment()  {

    private lateinit var binding: FragmentCargarPromocionPartnersBinding
    private val calendario = Calendar.getInstance()
    private val añoActual = calendario.get(Calendar.YEAR)
    private val mesActual = calendario.get(Calendar.MONTH)
    private val díaActual = calendario.get(Calendar.DAY_OF_MONTH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCargarPromocionPartnersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var usuario: UserPartner?
        var idComercio: String = ""
        var comercio: Comercio = Comercio(null, null, null, null, null)

        CoroutineScope(Dispatchers.Main).launch {
            usuario = Funciones().traerUsuarioPartner()
            if (usuario != null ) {
                if (usuario!!.idComercio != null) {
                    idComercio = usuario!!.idComercio!!
                    val lecturaComercio = LeerId().obtenerComercioPorId(idComercio)
                    if (lecturaComercio != null) {
                        comercio = lecturaComercio!!
                    }
                }
            }
        }

        val fechaDesde = view.findViewById<EditText>(R.id.editTextDesde)
        val fechaHasta = view.findViewById<EditText>(R.id.editTextHasta)
        fechaDesde.setOnClickListener { showDatePickerDialog(fechaDesde) }
        fechaHasta.setOnClickListener { showDatePickerDialog(fechaHasta) }
        val chipGroupDias = view.findViewById<ChipGroup>(R.id.chipGroupDias)
        val chipGroupTipoPromocion = view.findViewById<ChipGroup>(R.id.chipGroupTipoPromocion)
        val montoDto = view.findViewById<LinearLayout>(R.id.llMontoDescuento)
        val topeReintegro = view.findViewById<LinearLayout>(R.id.llTopeDeReintegro)
        val cantCuotas = view.findViewById<LinearLayout>(R.id.llCantidadCuotas)
        val sucursales = listOf<String>("Sucursal 1", "Sucursal 2", "Sucursal 3")
        val listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, sucursales)
        val listaSucursales = view.findViewById<ListView>(R.id.lvSucursales)
        listaSucursales.adapter = listAdapter

        chipGroupTipoPromocion.setOnCheckedStateChangeListener { group, checkedIds ->
            var chips = mutableListOf<String>()
            for (checkedId in checkedIds) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val tipoPromocion = selectedChip?.text.toString()
                chips.add(tipoPromocion)

            }
            if (chips.contains("Descuento")) {
                montoDto.visibility = View.VISIBLE
            } else {
                montoDto.visibility = View.GONE
            }
            if (chips.contains("Reintegro")) {
                topeReintegro.visibility = View.VISIBLE
            } else {
                topeReintegro.visibility = View.GONE
            }
            if (chips.contains("Cuotas")) {
                cantCuotas.visibility = View.VISIBLE

            } else {
                cantCuotas.visibility = View.GONE
            }
        }


        binding.llGuardarPromocion.setOnClickListener {
            val descPromo = view.findViewById<TextInputEditText>(R.id.tiDescripcionPromocion).text.toString().trim()
            var diasSeleccionados = mutableListOf<String>()
            var tipoPromocion = "Los tipos de promoción seleccionados son: "
            var cantidadCuotas = view.findViewById<EditText>(R.id.etCantidadCuotas).text.toString().trim()
            var montoDescuento = view.findViewById<EditText>(R.id.etMontoDescuento).text.toString().trim()
            var vigenciaDesde = fechaDesde.text.toString().trim()
            var vigenciaHasta = fechaHasta.text.toString().trim()
            var terminosYCondiciones = view.findViewById<TextInputEditText>(R.id.tiTerminosYCondiciones).text.toString().trim()
            var topeReintegro = view.findViewById<EditText>(R.id.etTopeDeReintegro).text.toString().trim()
            var topeReintegroTexto = "Tope de reintegro de " + topeReintegro + "."
            var tituloPromocion = comercio.nombre + ": promocion"
            for (index in 0 until chipGroupDias.childCount) {
                val chip = chipGroupDias.getChildAt(index) as Chip
                val diaChip = chip.text.toString()
                if (chip.isChecked) {
                    diasSeleccionados.add(diaChip)
                }
            }
            for (index in 0 until chipGroupTipoPromocion.childCount) {
                val chip = chipGroupTipoPromocion.getChildAt(index) as Chip
                val tipoPromoChip = chip.text.toString()
                if (chip.isChecked) {
                    tipoPromocion += tipoPromoChip + ", "
                }
            }

            var nuevaPromocion = PromocionEscritura(
                comercio.categoria!!,
                idComercio,
                cantidadCuotas,
                diasSeleccionados as List<String?>?,
                montoDescuento,
                "No posee",
                listOf("No posee"),
                listOf("No posee"),
                "descuento",
                tituloPromocion,
                topeReintegro,
                topeReintegroTexto,
                terminosYCondiciones,
                "ejemplo.com",
                vigenciaDesde,
                vigenciaHasta,
                "pendiente",
                null
            )
            CoroutineScope(Dispatchers.Main).launch {
                FuncionesPartners().escribirPromocion(nuevaPromocion)
            }

        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        // Configura el DatePickerDialog aquí
        // Por ejemplo:
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Formatea la fecha seleccionada y actualiza el EditText
                val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)
                editText.setText(selectedDate)
            },
            añoActual,
            mesActual,
            díaActual
        )
        datePickerDialog.show()
    }


}
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
import com.example.offerhub.R
import com.example.offerhub.databinding.FragmentCargarPromocionPartnersBinding
import com.example.offerhub.fragments.shopping.FilterFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
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
        val fechaDesde = view.findViewById<EditText>(R.id.editTextDesde)
        val fechaHasta = view.findViewById<EditText>(R.id.editTextHasta)
        fechaDesde.setOnClickListener { showDatePickerDialog(fechaDesde) }
        fechaHasta.setOnClickListener { showDatePickerDialog(fechaHasta) }
        val chipGroupDias = view.findViewById<ChipGroup>(R.id.chipGroupDias)
        val chipGroupTipoPromocion = view.findViewById<ChipGroup>(R.id.chipGroupTipoPromocion)
        val montoDto = view.findViewById<LinearLayout>(R.id.llMontoDescuento)
        val topeReintegro = view.findViewById<LinearLayout>(R.id.llTopeDeReintegro)
        val cantCuotas = view.findViewById<LinearLayout>(R.id.llCantidadCuotas)
        /*val listaSimboloDescuento = view.findViewById<AutoCompleteTextView>(R.id.tvTipoDescuento)
        val simboloDescuento = listOf("%", "$")
        val simboloDescuentoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, simboloDescuento)
        listaSimboloDescuento.setAdapter(simboloDescuentoAdapter)
        listaSimboloDescuento.setOnItemClickListener { parent, view, position, id ->
            val selectedOption = simboloDescuentoAdapter.getItem(position)
            listaSimboloDescuento.setText(selectedOption.toString())
        }*/

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
            val mensaje = "La promo va desde: " + fechaDesde.text.toString().trim() + " hasta: " + fechaHasta.text.toString().trim()
            var diasSeleccionados = "Los días seleccionados son: "
            var tipoPromocion = "Los tipos de promoción seleccionados son: "
            for (index in 0 until chipGroupDias.childCount) {
                val chip = chipGroupDias.getChildAt(index) as Chip
                val diaChip = chip.text.toString()
                if (chip.isChecked) {
                    diasSeleccionados += diaChip + ", "
                }
            }
            for (index in 0 until chipGroupTipoPromocion.childCount) {
                val chip = chipGroupTipoPromocion.getChildAt(index) as Chip
                val tipoPromoChip = chip.text.toString()
                if (chip.isChecked) {
                    tipoPromocion += tipoPromoChip + ", "
                }
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
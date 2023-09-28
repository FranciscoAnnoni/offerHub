package com.example.offerhub.fragments.shopping

import android.app.DatePickerDialog
import com.example.offerhub.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.Locale
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar


class FilterFragment : BottomSheetDialogFragment() {
    private val calendario = Calendar.getInstance()
    private val añoActual = calendario.get(Calendar.YEAR)
    private val mesActual = calendario.get(Calendar.MONTH)
    private val díaActual = calendario.get(Calendar.DAY_OF_MONTH)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño del filtro aquí

        // Configura tus elementos de filtro y acciones de clic aquí
        return inflater.inflate(R.layout.activity_filter, container, false)
    } // Agrega métodos y lógica para aplicar el filtro a tus datos

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val editTextDesde = view.findViewById<EditText>(R.id.editTextDesde)
        val editTextHasta = view.findViewById<EditText>(R.id.editTextHasta)
// Obtén una referencia al Spinner
        // Obtiene una referencia al ChipGroup
        val chipGroupTipoPromocion = view.findViewById<ChipGroup>(R.id.chipGroupTipoPromocion)

// Obtiene la lista de tipos de promoción desde strings.xml
        val tiposPromocion = resources.getStringArray(R.array.tipos_promocion)

// Itera sobre la lista y crea un chip para cada tipo de promoción
        for (tipo in tiposPromocion) {
            val chip = Chip(requireContext())
            chip.text = tipo
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_gray700)) // Color del texto
            chip.setChipBackgroundColorResource(com.google.android.material.R.color.mtrl_choice_chip_background_color) // Color del fondo del chip
            chip.isCheckable = true
            chip.isCheckedIconVisible = false
            chip.isClickable = true
            chipGroupTipoPromocion.addView(chip)
        }


        val datePickerDesde = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Aquí puedes manejar la fecha seleccionada y actualizar el texto en el EditText
            val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
            editTextDesde.setText(selectedDate)
        }

        val datePickerHasta = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Aquí puedes manejar la fecha seleccionada y actualizar el texto en el EditText
            val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
            editTextHasta.setText(selectedDate)
        }

        editTextDesde.setOnClickListener {
            // Muestra el diálogo de selección de fecha para "Desde"
            showDatePickerDialog(editTextDesde)
        }

        editTextHasta.setOnClickListener {
            // Muestra el diálogo de selección de fecha para "Hasta"
            showDatePickerDialog(editTextHasta)
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

    fun aplicarFiltros() {
        // Aquí puedes agregar la lógica para aplicar los filtros seleccionados.
        // Por ejemplo, puedes leer las selecciones del usuario de CheckBoxes, Spinners, etc.
        // Luego, aplica esos filtros a tus datos y realiza las acciones necesarias.

        // Ejemplo: Mostrar un mensaje de éxito
        Toast.makeText(requireContext(), "Filtros aplicados correctamente", Toast.LENGTH_SHORT).show()

        // Cierra la actividad de filtros
        dismiss()
    }


}

package com.example.offerhub.fragments.shopping

import SearchViewModel
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.offerhub.R
import com.example.offerhub.interfaces.FilterData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar
import java.util.Locale


class FilterFragment : BottomSheetDialogFragment() {
    private var currentFilters: FilterData? = null
    private val calendario = Calendar.getInstance()
    private val añoActual = calendario.get(Calendar.YEAR)
    public var filterListener: FilterListener? = null
    private val mesActual = calendario.get(Calendar.MONTH)
    private val díaActual = calendario.get(Calendar.DAY_OF_MONTH)
    private var selectedFilters: FilterData = FilterData("", "", mutableListOf(), mutableListOf())


    interface FilterListener {
        fun onFiltersApplied(filters: FilterData)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentFilters = it.getParcelable("currentFilters")
        }
    }
    fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño del filtro aquí
        val view = inflater.inflate(R.layout.activity_filter, container, false)

        // Resto de tu código para el FilterFragment

        return view
        // Configura tus elementos de filtro y acciones de clic aquí
        //return inflater.inflate(R.layout.activity_filter, container, false)
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
        var viewModel: SearchViewModel =
            ViewModelProvider(requireActivity()).get<SearchViewModel>(SearchViewModel::class.java)

// Itera sobre la lista y crea un chip para cada tipo de promoción
        for (tipo in tiposPromocion) {
            val chip = Chip(requireContext())
            chip.text = tipo
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_gray700)) // Color del texto
            chip.setChipBackgroundColorResource(com.google.android.material.R.color.mtrl_choice_chip_background_color) // Color del fondo del chip
            chip.isCheckable = true
            chip.isChecked=tipo in viewModel.filtrosActuales?.tiposPromocion ?: mutableListOf()
            chip.isCheckedIconVisible = false
            chip.isClickable = true
            chipGroupTipoPromocion.addView(chip)
        }



        // Supongamos que tienes un objeto FilterData llamado "currentFilters" que contiene los filtros actuales
        val currentFilters = viewModel.obtenerFiltrosActuales()
        if(currentFilters!=null) {
            Log.d("MiApp", "Contenido de FilterData: " + currentFilters.toString());
            // Cargar los valores de vigencia si existen
            val editTextDesde = view.findViewById<EditText>(R.id.editTextDesde)
            val editTextHasta = view.findViewById<EditText>(R.id.editTextHasta)

            if (!currentFilters!!.desde.isNullOrEmpty()) {
                editTextDesde.text = currentFilters!!.desde.toEditable()
            }

            if (!currentFilters.hasta.isNullOrEmpty()) {
                editTextHasta.text = currentFilters.hasta.toEditable()
            }

            // Cargar los valores de días de promoción si existen
            val chipGroupDias = view.findViewById<ChipGroup>(R.id.chipGroupDias)

            for (index in 0 until chipGroupDias.childCount) {
                val chip = chipGroupDias.getChildAt(index) as Chip
                val díaChip = chip.text.toString()

                // Verifica si el día del chip está presente en currentFilters.diasPromocion
                val isSelected = currentFilters.diasPromocion.contains(díaChip)

                // Marca el chip como seleccionado si corresponde
                chip.isChecked = isSelected
            }
            selectedFilters=currentFilters
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

        editTextDesde.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Actualiza selectedFilters.desde cuando se cambia el texto
                selectedFilters.desde = s.toString()
                viewModel.actualizarFiltrosActuales(selectedFilters)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })

        editTextHasta.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Actualiza selectedFilters.hasta cuando se cambia el texto
                selectedFilters.hasta = s.toString()
                viewModel.actualizarFiltrosActuales(selectedFilters)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })


        val btnReset = view.findViewById<CircularProgressButton>(R.id.btnReset)
        btnReset.setOnClickListener {
            // Restablece los filtros seleccionados
            selectedFilters = FilterData("", "", mutableListOf(), mutableListOf())
            viewModel.actualizarFiltrosActuales(selectedFilters)

            // Aquí puedes limpiar las vistas de selección de filtros, por ejemplo, deseleccionar chips o borrar texto en los EditText
            // También puedes ocultar esta vista si es necesario

            // Notifica a la actividad contenedora (SearchFragment) que se han aplicado los filtros
            filterListener?.onFiltersApplied(selectedFilters)

            // Cierra el fragmento de filtro
            dismiss()
        }

        val btnAplicar = view.findViewById<CircularProgressButton>(R.id.btnAplicar)
        btnAplicar.setOnClickListener {
            // Aquí obtén los valores de los filtros seleccionados desde las vistas (EditText, Chips, etc.)
            // Llena selectedFilters con los valores seleccionados

            // Notifica a la actividad contenedora (SearchFragment) que se han aplicado los filtros
            filterListener?.onFiltersApplied(selectedFilters)

            // Cierra el fragmento de filtro
            dismiss()
        }
        val chipGroupDias = view.findViewById<ChipGroup>(R.id.chipGroupDias)

// Escuchador para los chips de tipo de promoción
        chipGroupTipoPromocion.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedFilters.tiposPromocion= mutableListOf()
            for (checkedId in checkedIds) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val tipoPromocion = selectedChip?.text.toString()

                selectedFilters.tiposPromocion.add(tipoPromocion)
            }
            viewModel.actualizarFiltrosActuales(selectedFilters)
        }

// Escuchador para los chips de días de promoción
        chipGroupDias.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedFilters.diasPromocion= mutableListOf()
            for (checkedId in checkedIds) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val díaSeleccionado = selectedChip?.text.toString()

                selectedFilters.diasPromocion.add(díaSeleccionado)
            }
            viewModel.actualizarFiltrosActuales(selectedFilters)
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

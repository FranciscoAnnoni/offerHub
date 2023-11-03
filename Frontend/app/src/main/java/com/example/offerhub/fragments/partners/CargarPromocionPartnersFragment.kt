package com.example.offerhub.fragments.partners

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.offerhub.Funciones
import com.example.offerhub.PromocionEscritura
import com.example.offerhub.R
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Comercio
import com.example.offerhub.LeerId
import com.example.offerhub.Promocion
import com.example.offerhub.data.UserPartner
import com.example.offerhub.databinding.FragmentCargarPromocionPartnersBinding
import com.example.offerhub.funciones.FuncionesPartners
import com.example.offerhub.interfaces.OnAddItemListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class CargarPromocionPartnersFragment: Fragment(), OnAddItemListener  {

    private lateinit var binding: FragmentCargarPromocionPartnersBinding
    private val calendario = Calendar.getInstance()
    private val añoActual = calendario.get(Calendar.YEAR)
    private val mesActual = calendario.get(Calendar.MONTH)
    private val díaActual = calendario.get(Calendar.DAY_OF_MONTH)
    private val sucursalesList = mutableListOf<String>()


    fun transformarFecha(fechaOriginal: String): String {
        val formatoOriginal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if(fechaOriginal.isNotEmpty()) {
            val fecha = formatoOriginal.parse(fechaOriginal)

            val formatoNuevo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatoNuevo.format(fecha)
        }
        return ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCargarPromocionPartnersBinding.inflate(inflater)
        return binding.root
    }
    fun updateSucursalesList(newItem: String) {
        sucursalesList.add(newItem)
        (binding.lvSucursales.adapter as ArrayAdapter<String>).notifyDataSetChanged()
    }

    fun obtenerSucursalesChequeadas(listView: ListView): List<String> {
        val elementosSeleccionados = mutableListOf<String>()

        val itemCount = listView.count
        val checkedItemPositions = listView.checkedItemPositions

        for (i in 0 until itemCount) {
            val isChecked = checkedItemPositions.get(i)
            if (isChecked) {
                val item = listView.getItemAtPosition(i) as String
                elementosSeleccionados.add(item)
            }
        }

        return elementosSeleccionados
    }

    fun marcarSucursales(listView: ListView, sucursales: List<String?>){
        val itemCount = listView.count

        for (i in 0 until itemCount) {
            if (sucursales.contains(listView.getItemAtPosition(i))){
                listView.setItemChecked(i, true)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var usuario: UserPartner?
        var idComercio: String = ""
        var comercio: Comercio = Comercio()

        val promocionAnterior = arguments?.getParcelable("promocion") as? Promocion
        val isEditing = arguments?.getBoolean("isEditing", false) ?: false

        val fechaDesde = view.findViewById<EditText>(R.id.editTextDesde)
        val fechaHasta = view.findViewById<EditText>(R.id.editTextHasta)
        fechaDesde.setOnClickListener { showDatePickerDialog(fechaDesde) }
        fechaHasta.setOnClickListener { showDatePickerDialog(fechaHasta) }
        val chipGroupDias = view.findViewById<ChipGroup>(R.id.chipGroupDias)
        val chipGroupTipoPromocion = view.findViewById<ChipGroup>(R.id.chipGroupTipoPromocion)
        val montoDto = view.findViewById<LinearLayout>(R.id.llMontoDescuento)
        val montoDtotext = view.findViewById<EditText>(R.id.montoDto)
        val topeReintegro = view.findViewById<LinearLayout>(R.id.llTopeDeReintegro)
        val topeReintegrotext = view.findViewById<EditText>(R.id.topeReintegro)
        val cantCuotas = view.findViewById<LinearLayout>(R.id.llCantidadCuotas)
        val cuotastext = view.findViewById<EditText>(R.id.cantidadCuotas)
        val tituloPromo = view.findViewById<TextInputEditText>(R.id.tiTituloPromocion)
        val descPromo = view.findViewById<TextInputEditText>(R.id.tiDescripcionPromocion)
        val tycPromo = view.findViewById<TextInputEditText>(R.id.tiTerminosYCondiciones)
        val listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, sucursalesList)
        val listaSucursales = view.findViewById<ListView>(R.id.lvSucursales)
        listaSucursales.adapter = listAdapter


        fun habilitarTipos(chips: MutableList<String>){
            if (chips.contains("Descuento") || chips.contains("Reintegro")) {
                montoDto.visibility = View.VISIBLE
            } else {
                montoDto.visibility = View.GONE
            }
            if (chips.contains("Reintegro")) {
                topeReintegro.visibility = View.VISIBLE
            } else {
                topeReintegro.visibility = View.GONE
            }
            if (chips.contains("Cuotas") || chips.contains("Descuento") || chips.contains("Reintegro")) {
                cantCuotas.visibility = View.VISIBLE

            } else {
                cantCuotas.visibility = View.GONE
            }
        }
        if (isEditing){
            binding.llGuardarPromocion.visibility = View.GONE
            if (promocionAnterior != null) {
                if (promocionAnterior.vigenciaDesde!= null){
                    fechaDesde.text = Editable.Factory.getInstance().newEditable(promocionAnterior.vigenciaDesde.toString())
                }
                if (promocionAnterior.cuotas !=null){
                    cuotastext.text = Editable.Factory.getInstance().newEditable(promocionAnterior.cuotas.toString())
                }
                if (promocionAnterior.descripcion!= null){
                    descPromo.text = Editable.Factory.getInstance().newEditable(promocionAnterior.descripcion.toString())
                }
                if (promocionAnterior.tyc != null){
                    tycPromo.text = Editable.Factory.getInstance().newEditable(promocionAnterior.tyc.toString())
                }
                if (promocionAnterior.topeNro != null){
                    topeReintegrotext.text = Editable.Factory.getInstance().newEditable(promocionAnterior.topeNro.toString())
                }
                if (promocionAnterior.porcentaje != null){
                    montoDtotext.text = Editable.Factory.getInstance().newEditable(promocionAnterior.porcentaje.toString())
                }
                fechaHasta.text = Editable.Factory.getInstance().newEditable(promocionAnterior.vigenciaHasta.toString())
                tituloPromo.text = Editable.Factory.getInstance().newEditable(promocionAnterior.titulo.toString())
                for (index in 0 until chipGroupDias.childCount) {
                    val chip = chipGroupDias.getChildAt(index) as Chip
                    val diaChip = chip.text.toString()
                    if (promocionAnterior.dias?.contains(diaChip) == true){
                        chip.isChecked = true
                    }
                }
                var chips = mutableListOf<String>()
                for (index in 0 until chipGroupTipoPromocion.childCount) {
                    val chip = chipGroupTipoPromocion.getChildAt(index) as Chip
                    val tipoPromoChip = chip.text.toString()
                    if (promocionAnterior.tipoPromocion?.contains(tipoPromoChip)!!){
                        chip.isChecked = true
                        chips.add(tipoPromoChip)
                    }
                }
                habilitarTipos(chips)
            }

            listaSucursales.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                // Acción que se realiza cuando se selecciona un elemento
                binding.llGuardarPromocion.visibility = View.VISIBLE
            }

            tituloPromo.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            for (i in 0 until chipGroupDias.childCount) {
                val chip = chipGroupDias.getChildAt(i) as Chip
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
            }

            for (i in 0 until chipGroupTipoPromocion.childCount) {
                val chip = chipGroupTipoPromocion.getChildAt(i) as Chip
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
            }

            cuotastext.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            topeReintegrotext.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            montoDtotext.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            descPromo.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            tycPromo.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }


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
            if (comercio.sucursales != null && comercio.sucursales!!.isNotEmpty()) {
                for (sucursal in comercio.sucursales!!) {
                    if (sucursal != null) {
                        updateSucursalesList(sucursal)
                    }
                }
            }

            if(isEditing){
                if (promocionAnterior != null) {
                    if (promocionAnterior.sucursales !=null && promocionAnterior.sucursales!!.isNotEmpty()){
                        marcarSucursales(listaSucursales, promocionAnterior.sucursales!!)
                    }
                }
                listaSucursales.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    // Acción que se realiza cuando se selecciona un elemento
                    binding.llGuardarPromocion.visibility = View.VISIBLE
                }

            }
        }


        chipGroupTipoPromocion.setOnCheckedStateChangeListener { group, checkedIds ->
            var chips = mutableListOf<String>()
            for (checkedId in checkedIds) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val tipoPromocion = selectedChip?.text.toString()
                chips.add(tipoPromocion)
            }
            habilitarTipos(chips)

        }
        val imageClose =  view.findViewById<ImageView>(R.id.imageClose)
        imageClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.botonAgregarSucursal.setOnClickListener {
            val bottomSheetDialog = AgregarSucursalFragment.newInstance(comercio,this)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "AgregarSucursal")
        }
        binding.llGuardarPromocion.setOnClickListener {

            //agregar chequeo que no queden en null fecha fecha hasta, tipoPromocion, etc
            val mensaje = "La promo va desde: " + fechaDesde.text.toString().trim() + " hasta: " + fechaHasta.text.toString().trim()
            var diasSeleccionados = "Los días seleccionados son: "
            var tipoPromocion = ""
            var desde: String? = null
            var hasta: String? = null
            try{
                desde = transformarFecha(fechaDesde.text.toString().trim())
                hasta = transformarFecha(fechaHasta.text.toString().trim())
            }catch (e: Exception) {
                desde = fechaDesde.text.toString()
                hasta = fechaHasta.text.toString()
            }

            var diasLista: MutableList<String?> = mutableListOf()
            var tituloPromocion = comercio.nombre +": " + tituloPromo
            for (index in 0 until chipGroupDias.childCount) {
                val chip = chipGroupDias.getChildAt(index) as Chip
                val diaChip = chip.text.toString()
                if (chip.isChecked) {
                    diasLista.add(diaChip)
                    diasSeleccionados += diaChip + ", "
                }
            }
            for (index in 0 until chipGroupTipoPromocion.childCount) {
                val chip = chipGroupTipoPromocion.getChildAt(index) as Chip
                val tipoPromoChip = chip.text.toString()
                if (chip.isChecked) {
                    tipoPromocion = tipoPromoChip
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                val usuario = Funciones().traerUsuarioPartner()

                var promocion = PromocionEscritura(comercio.categoria, usuario?.idComercio,null,diasLista,null,null,null,null,tipoPromocion,tituloPromo.text.toString().trim(),null,null,tycPromo.text.toString().trim(),null,descPromo.text.toString().trim(),desde,hasta ,"pendiente")

                if (cuotastext.text.isNotEmpty()){
                    promocion.cuotas = cuotastext.text.toString().trim()
                }
                if (montoDtotext.text.isNotEmpty()){
                    promocion.porcentaje = montoDtotext.text.toString().trim()
                }
                if (topeReintegrotext.text.isNotEmpty()){
                    promocion.topeNro = topeReintegrotext.text.toString().trim()
                    promocion.topeTexto = "Tope de Reintegro: $"+topeReintegrotext.text.toString().trim()
                }
                var sucursales=obtenerSucursalesChequeadas(listaSucursales)
                if (sucursales.isNotEmpty()){
                    promocion.sucursales = sucursales
                }
                view.findViewById<TextView>(R.id.errorTitulo).visibility=View.GONE
                view.findViewById<TextView>(R.id.errorTitulo).text=""
                view.findViewById<TextView>(R.id.errorVigencia).visibility=View.GONE
                view.findViewById<TextView>(R.id.errorVigencia).text=""
                view.findViewById<TextView>(R.id.errorVigencia).visibility=View.GONE
                view.findViewById<TextView>(R.id.errorVigencia).text=""
                view.findViewById<TextView>(R.id.errorTipoPromo).visibility=View.GONE
                view.findViewById<TextView>(R.id.errorTipoPromo).text=""
                view.findViewById<TextView>(R.id.errorDias).visibility=View.GONE
                view.findViewById<TextView>(R.id.errorDias).text=""
                var errores: List<MutableList<String>> = mutableListOf()
                if (!isEditing){
                    errores = promocion.validar()
                }
                if (!errores.isEmpty() && errores[0].size > 0) {
                    var campos=errores[0]
                    var error=errores[1]
                    var i=0
                    for (i in 0 until campos.size) {
                        val campoId = resources.getIdentifier(campos[i], "id", requireContext().packageName)
                        val campo = view.findViewById<TextView>(campoId)
                        campo.text = if (campo.text.isNotEmpty()) campo.text.toString() + " " + error[i] else error[i]
                        campo.visibility = View.VISIBLE
                    }
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Errores")
                        .setMessage("Por favor revise los errores indicados y vuelva a intentar.")
                        .setCancelable(false)
                        .show()
                    Handler().postDelayed({
                        alertDialog.dismiss()
                    }, 3000)
                } else {
                    promocion.vigenciaDesde=if(promocion.vigenciaDesde!!.isEmpty()) "No posee" else promocion.vigenciaDesde
                    promocion.vigenciaHasta=if(promocion.vigenciaHasta!!.isEmpty()) "No posee" else promocion.vigenciaHasta
                    FuncionesPartners().escribirPromocion(promocion)
                    if (isEditing){
                        val database = FirebaseDatabase.getInstance("https://offerhub-proyectofinal-default-rtdb.firebaseio.com")
                        val promocionRef =
                            promocionAnterior?.id?.let { it1 -> database.getReference("/Promocion").child(it1) }

                        if (promocionRef != null) {
                            promocionRef.removeValue()
                        }
                    }

                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Promocion guardada")
                        .setMessage("La promocion se ha guardado exitosamente")
                        .setCancelable(false)
                        .show()


                    Handler().postDelayed({
                        alertDialog.dismiss()
                    }, 3000)

                    findNavController().popBackStack()
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

    override fun onAddItem(item: String) {
        updateSucursalesList(item)

    }


}
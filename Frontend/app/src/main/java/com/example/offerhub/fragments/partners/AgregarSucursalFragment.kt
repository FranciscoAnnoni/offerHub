package com.example.offerhub.fragments.partners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.offerhub.Comercio
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Sucursal
import com.example.offerhub.SucursalEscritura
import com.example.offerhub.databinding.FragmentAgregarSucursalBinding
import com.example.offerhub.fragments.shopping.CompararFragment
import com.example.offerhub.fragments.shopping.CompararFragmentArgs
import com.example.offerhub.funciones.FuncionesPartners
import com.example.offerhub.interfaces.OnAddItemListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class AgregarSucursalFragment : BottomSheetDialogFragment() {
    private val args by navArgs<AgregarSucursalFragmentArgs>()

    companion object {
        fun newInstance(comercio: Comercio,listener: OnAddItemListener): AgregarSucursalFragment {
            val fragment = AgregarSucursalFragment()
            val args = Bundle()
            args.putParcelable("comercio", comercio)
            fragment.setAddItemListener(listener)
            fragment.arguments = args
            return fragment
        }
    }
    private var addItemListener: OnAddItemListener? = null

    fun setAddItemListener(listener: OnAddItemListener) {
        addItemListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_sucursal, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val comercio = arguments?.getParcelable<Comercio>("comercio")
        val imageClose =  view.findViewById<ImageView>(R.id.imageClose)
        val llGuardarSucursal =  view.findViewById<LinearLayout>(R.id.llGuardarSucursal)
        imageClose.setOnClickListener {
            dismiss()
        }
        llGuardarSucursal.setOnClickListener {
            val tiDireccion = view.findViewById<TextInputEditText>(R.id.tiDireccion).text.toString().trim()
            if(tiDireccion.length>0){
                if (comercio != null) {
                    FuncionesPartners().agregarSucursalAComercio(comercio.id.toString(),tiDireccion)
                    addItemListener?.onAddItem(tiDireccion)
                    dismiss()
                }
            } else {
                val campo = view.findViewById<TextView>(R.id.errorDireccion)
                campo.text = "Direccion no puede estar vacia."
                campo.visibility = View.VISIBLE
            }
        }


    }
}
package com.example.offerhub.activities

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
import com.example.offerhub.data.UserPartner
import com.example.offerhub.funciones.FuncionesPartners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaSucursalAdapter(private val context: Context, private val sucursales: MutableList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return sucursales.size
    }

    override fun getItem(position: Int): Any {
        return sucursales[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var usuario: UserPartner?
        var idComercio = ""

        CoroutineScope(Dispatchers.Main).launch {
            usuario = Funciones().traerUsuarioPartner()
            if (usuario != null) {
                if (usuario!!.idComercio != null) {
                    idComercio = usuario!!.idComercio!!

                }
            }
        }

        val sucursal = getItem(position) as String



        // Aquí puedes inflar un diseño personalizado para cada elemento de la lista
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.item_lista_sucursal, parent, false)

        // Configura las vistas en el diseño personalizado con los datos de la Tarjeta
        val textViewTitulo = itemView.findViewById<TextView>(R.id.tvNombreSucursal)
        textViewTitulo.text = sucursal

        val botonEliminar = itemView.findViewById<ImageButton>(R.id.deleteButton)


        botonEliminar.setOnClickListener {

            Log.d("Dentro de eliminar sucursal, ID COMERCIO: ", idComercio)
            CoroutineScope(Dispatchers.Main).launch {
                FuncionesPartners().eliminarSucursalDeComercio(idComercio, sucursal)
            }

            this.removeSucursal(sucursal)
            this.notifyDataSetChanged()

            Toast.makeText(context, "La sucursal: $sucursal fue eliminada", Toast.LENGTH_SHORT).show()

        }


        // Puedes configurar otras vistas aquí según las propiedades de la Tarjeta
        return itemView
    }

    fun removeSucursal(sucursal: String) {
        sucursales.remove(sucursal)
    }
}

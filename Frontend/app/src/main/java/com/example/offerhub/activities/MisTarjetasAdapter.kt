package com.example.offerhub.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.getSystemService
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MisTarjetasAdapter(private val context: Context, private val tarjetasUsuario: List<Tarjeta>): BaseAdapter(){

    override fun getCount(): Int {
        return tarjetasUsuario.size
    }

    override fun getItem(position: Int): Any {
        return tarjetasUsuario[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tarjeta = getItem(position) as Tarjeta
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridViewItem = inflater.inflate(R.layout.fragment_mi_tarjeta_card, null)
        var leerBD = LeerId()
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        var segmento = tarjeta.segmento!!
        var tipo = tarjeta.tipoTarjeta!!
        var procesadora = tarjeta.procesadora!!
        var tarjetaDescripcion = ""
        if (segmento != "No posee") {
            tarjetaDescripcion +=  segmento + " "
        }
        if(tipo != "No posee" && tipo != "Fidelidad") {
            tarjetaDescripcion += tipo + " "
        }
        if(procesadora != "No posee") {
            tarjetaDescripcion += procesadora
        }

        val tvNombreEntidad = gridViewItem.findViewById<TextView>(R.id.tvNombreEntidad)
        val descripcionTarjeta = gridViewItem.findViewById<TextView>(R.id.tvTarjetaDescripcion)
        val job = coroutineScope.launch {
            val entidad = leerBD.obtenerEntidadPorId(tarjeta.entidad!!)
            if (entidad != null ){
                tvNombreEntidad.text = entidad.nombre
                descripcionTarjeta.text = tarjetaDescripcion
            }

        }

        return gridViewItem
    }
}
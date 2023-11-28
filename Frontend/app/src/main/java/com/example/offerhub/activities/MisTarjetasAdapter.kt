package com.example.offerhub.activities

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
import com.example.offerhub.Usuario
import com.example.offerhub.databinding.FragmentMisTarjetasBinding
import com.example.offerhub.fragments.settings.MisTarjetasFragment
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.viewmodel.UserViewModelCache
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MisTarjetasAdapter(
    private val context: Context,
    val tarjetasUsuario: MutableList<Tarjeta>

    ): BaseAdapter(){
    private var uvm = UserViewModelSingleton.getUserViewModel()
    private lateinit var usuario: Usuario


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
       /* if(procesadora != "No posee") {
            tarjetaDescripcion += procesadora
        }*/

        val tvNombreEntidad = gridViewItem.findViewById<TextView>(R.id.tvNombreEntidad)
        val descripcionTarjeta = gridViewItem.findViewById<TextView>(R.id.tvTarjetaDescripcion)
        val logoBanco = gridViewItem.findViewById<ImageView>(R.id.ivLogoBanco)
        val logoEmisora = gridViewItem.findViewById<ImageView>(R.id.logoEmisora)
        if (tarjeta.procesadora !=null) {
            val procesadora= tarjeta.procesadora!!.lowercase().replace(" ","")
            val imageId = context.getResources()
                .getIdentifier("logo_"+procesadora, "drawable", context.getPackageName())
            if (imageId != 0) {
                val drawable: Drawable? = context.getDrawable(imageId)

                logoEmisora.setImageDrawable(drawable)

            } else {
                // Si el drawableId es 0, significa que no se encontró el recurso
                // Puedes manejar este caso según tus necesidades, por ejemplo, establecer una imagen de respaldo.
            }
        }
        val job = coroutineScope.launch {
            val entidad = leerBD.obtenerEntidadPorId(tarjeta.entidad!!)
            var entidadNombre: String = ""
            if (entidad != null ){
                tvNombreEntidad.text = entidad.nombre
                descripcionTarjeta.text = tarjetaDescripcion
                entidadNombre = entidad.nombre!!
            }

            var nombreDrawable = "logo_" + removeAccents(entidadNombre.replace(" ", "_")?.lowercase())

            Log.d("nombre logo",nombreDrawable)
            val drawableId: Int = context.getResources()
                .getIdentifier(nombreDrawable, "drawable", context.getPackageName())

            if (drawableId != 0) {
                val drawable: Drawable? = context.getDrawable(drawableId)
                logoBanco.setImageDrawable(drawable)

            } else {
                // Si el drawableId es 0, significa que no se encontró el recurso
                // Puedes manejar este caso según tus necesidades, por ejemplo, establecer una imagen de respaldo.
            }

        }

        gridViewItem.findViewById<ImageView>(R.id.ivBorrarTarjeta).setOnClickListener {
            uvm = UserViewModelSingleton.getUserViewModel()
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            var funciones = Funciones()


            coroutineScope.launch {
                funciones.eliminarTodasLasTarjetasDeUsuario(uvm.usuario!!.id)
                uvm.usuario!!.tarjetas!!.remove(tarjeta.id)
                if (uvm.usuario!!.tarjetas != null) {
                    if(uvm.usuario!!.tarjetas!!.isNotEmpty()) {
                        funciones.agregarTarjetasAUsuario(
                            uvm.usuario!!.id,
                            uvm.usuario!!.tarjetas!! as MutableList<String>
                        )
                    }
                }

                UserViewModelCache().guardarUserViewModel(uvm)
                uvm.listadoDePromosDisp=uvm.listadoDePromosDisp.filterNot { promo ->
                    (promo.tarjetas?.contains(tarjeta.id) == true)
                }
                UserViewModelCache().guardarUserViewModel(uvm)

            }

            this.removeTarjeta(tarjeta)

            // Notifica al adaptador que los datos han cambiado
            this.notifyDataSetChanged()

            Toast.makeText(context, "Tarjeta Eliminada", Toast.LENGTH_LONG).show()

        }


        return gridViewItem



    }

    fun removeTarjeta(tarjeta: Tarjeta) {
        tarjetasUsuario.remove(tarjeta)
    }

}
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.InterfaceSinc
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.Tarjeta
import com.example.offerhub.data.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TarjetasPromocionAdapter(private val tarjetasPromocionList: List<String>?) : RecyclerView.Adapter<TarjetasPromocionAdapter.TarjetaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarjetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tarjeta_card, parent, false)
        return TarjetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarjetaViewHolder, position: Int) {

        val instancia= Funciones()
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            val idTarjeta = tarjetasPromocionList?.get(position).toString()
            // Configura los elementos de tu tarjeta según los datos de 'tarjeta'
            // Ejemplo:
            // holder.tituloTextView.text = tarjeta.titulo
            // holder.descripcionTextView.text = tarjeta.descripcion
            holder.txtBanco.text = idTarjeta
        }
    }

    override fun getItemCount(): Int {
        return tarjetasPromocionList?.size ?:0
    }

    inner class TarjetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define las vistas de tu tarjeta aquí
        // Ejemplo:
        // val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        // val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        var txtBanco=itemView.findViewById<TextView>(R.id.txtBanco)
    }
}


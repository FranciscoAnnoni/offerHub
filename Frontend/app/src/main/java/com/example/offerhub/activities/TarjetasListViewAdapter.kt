import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.offerhub.R
import com.example.offerhub.Tarjeta


class TarjetasListViewAdapter(private val context: Context, private val tarjetas: List<Tarjeta>, tarjetasUsuario: MutableList<String?>) : BaseAdapter() {
    var tarjetasUsuario = tarjetasUsuario

    override fun getCount(): Int {
        return tarjetas.size
    }

    override fun getItem(position: Int): Any {
        return tarjetas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val tarjeta = getItem(position) as Tarjeta

        var segmento = tarjeta.segmento!!
        var tipo = tarjeta.tipoTarjeta!!
        var procesadora = tarjeta.procesadora!!
        var tarjetaTitulo = ""
        if (segmento != "No posee") {
            tarjetaTitulo +=  segmento + " "
        }
        if(tipo != "No posee" && tipo != "Fidelidad") {
            tarjetaTitulo += tipo + " "
        }
        if(procesadora != "No posee") {
            tarjetaTitulo += procesadora
        }

        // Aquí puedes inflar un diseño personalizado para cada elemento de la lista
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.fragment_opcion_tarjeta, parent, false)

        // Configura las vistas en el diseño personalizado con los datos de la Tarjeta
        val textViewTitulo = itemView.findViewById<TextView>(R.id.tvOpcionTarjeta)
        textViewTitulo.text = tarjetaTitulo

        if (tarjetasUsuario.contains(tarjeta.id)) {
            val imagen = itemView.findViewById<ImageView>(R.id.ivOpcionTarjeta)
            imagen.setImageResource(R.drawable.full_checkbox)
        }

        // Puedes configurar otras vistas aquí según las propiedades de la Tarjeta
        return itemView
    }
}

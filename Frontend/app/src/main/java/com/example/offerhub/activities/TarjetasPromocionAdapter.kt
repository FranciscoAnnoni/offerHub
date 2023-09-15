import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.offerhub.Funciones
import com.example.offerhub.LeerId
import com.example.offerhub.R
import com.example.offerhub.funciones.removeAccents
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
        val instanciaLeerID= LeerId()
        val context = holder.itemView.context
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        // Llamar a la función que obtiene los datos.
        val job = coroutineScope.launch {
            val idTarjeta = tarjetasPromocionList?.get(position).toString()
            val tarjeta=instanciaLeerID.obtenerTarjetaPorId(idTarjeta)
            if (tarjeta != null) {
                if(tarjeta.entidad!=null){
                    val entidad = instancia.traerNombeEntidad(tarjeta.entidad)
                    if (entidad != null) {
                        holder.txtBanco.text = entidad.uppercase()

                        var nombreDrawable = "logo_" + removeAccents(entidad?.replace(" ", "_")?.lowercase())
                        val drawableId: Int = context.getResources()
                            .getIdentifier(nombreDrawable, "drawable", context.getPackageName())

                        if (drawableId != 0) {
                            val drawable: Drawable? = context.getDrawable(drawableId)

                            holder.imgBanco.setImageDrawable(drawable)
                        } else {
                            // Si el drawableId es 0, significa que no se encontró el recurso
                            // Puedes manejar este caso según tus necesidades, por ejemplo, establecer una imagen de respaldo.
                        }
                    }
                }
                if(tarjeta.segmento!="No posee"){
                    holder.txtSegmento.text=tarjeta.segmento
                } else{
                    holder.txtSegmento.visibility=View.GONE
                }
                if(tarjeta.procesadora!="No posee"){
                    holder.txtProcesadora.text=tarjeta.procesadora
                } else{
                    holder.txtProcesadora.visibility=View.GONE
                }
                if(tarjeta.tipoTarjeta!="Fidelidad"){
                    holder.txtTipoTarjeta.text=tarjeta.tipoTarjeta
                } else{
                    holder.txtTipoTarjeta.visibility=View.GONE
                }
            }
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
        var imgBanco=itemView.findViewById<ImageView>(R.id.imgBanco)
        var txtBanco=itemView.findViewById<TextView>(R.id.txtBanco)
        var txtProcesadora=itemView.findViewById<TextView>(R.id.txtProcesadora)
        var txtSegmento=itemView.findViewById<TextView>(R.id.txtSegmento)
        var txtTipoTarjeta=itemView.findViewById<TextView>(R.id.txtTipoTarjeta)
    }
}


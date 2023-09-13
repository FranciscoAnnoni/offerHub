import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.funciones.getFavResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridAdapter(private val context: Context, private val promociones: List<Promocion>) : BaseAdapter() {

    override fun getCount(): Int {
        return promociones.size
    }

    override fun getItem(position: Int): Any {
        return promociones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       val promocion = getItem(position) as Promocion

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //val gridViewItem = inflater.inflate(R.layout.fragment_promo_card, null)
        val gridViewItem = inflater.inflate(R.layout.fragment_promocion_card, null)
        val instancia=Funciones()
        val textViewCategory = gridViewItem.findViewById<TextView>(R.id.txtCategoria)
        val favIcon = gridViewItem.findViewById<ImageView>(R.id.promoFav)
        val imgViewCategory = gridViewItem.findViewById<ImageView>(R.id.imgComercio)

        textViewCategory.text = promocion.titulo
       // imgViewCategory.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cat_default))
        // Utiliza la función base64ToBitmap para obtener el Bitmap del logotipo
        /*
        textViewCategory.text = comercio.nombre */
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            val logoBitmap = Comercio(
                "",
                "",
                "",
                ""
            ).base64ToBitmap(Funciones().traerLogoComercio(promocion.comercio))
            if (logoBitmap != null) {
                imgViewCategory.setImageBitmap(logoBitmap)
            }
            val isFavorite= instancia.traerUsuarioActual()
                ?.let { instancia.existePromocionEnFavoritos(it,promocion.id) } == true
            favIcon.setImageResource(getFavResource(isFavorite))
        }

        // Agrega cualquier otra configuración específica de tu diseño aquí

        return gridViewItem
    }
}

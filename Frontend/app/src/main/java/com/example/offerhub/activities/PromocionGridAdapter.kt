import android.content.Context
import android.graphics.Bitmap
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
import com.example.offerhub.Usuario
import com.example.offerhub.funciones.base64ToBitmap
import com.example.offerhub.funciones.getFavResource
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridAdapter(private val context: Context, private val promociones: List<Promocion>,
                           private val userViewModel: UserViewModel) : BaseAdapter() {

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
        val tvComercio = gridViewItem.findViewById<TextView>(R.id.tvPromocionComercio)
        val tvDescuento = gridViewItem.findViewById<TextView>(R.id.tvPromocionDescuento)
        val favIcon = gridViewItem.findViewById<ImageView>(R.id.promoFav)
        val imgViewCategory = gridViewItem.findViewById<ShapeableImageView>(R.id.imgComercio)

       // tvComercio.text = instancia.traerInfoComercio(promocion.comercio)
    promocion.obtenerDesc()
        if (promocion.porcentaje == null) {
            tvDescuento.text =promocion.tipoPromocion
        } else {
            tvDescuento.text = promocion.porcentaje + "%"
        }

       // imgViewCategory.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cat_default))
        // Utiliza la función base64ToBitmap para obtener el Bitmap del logotipo
        /*
        textViewCategory.text = comercio.nombre */
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            tvComercio.text = instancia.traerInfoComercio(promocion.comercio, "nombre")
        }
        var logoBitmap: Bitmap? =null
        coroutineScope.launch {
            logoBitmap = base64ToBitmap(Funciones().traerLogoComercio(promocion.comercio))

        }.invokeOnCompletion {
            coroutineScope.launch {
            if (logoBitmap != null) {
                imgViewCategory.setImageBitmap(logoBitmap)
            }
            val isFavorite= userViewModel.favoritos?.contains(promocion.id) == true
            favIcon.setImageResource(getFavResource(isFavorite))
            }
        }


        return gridViewItem
    }
}

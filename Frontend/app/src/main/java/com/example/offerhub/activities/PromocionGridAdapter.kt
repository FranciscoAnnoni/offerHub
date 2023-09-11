import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.offerhub.Comercio
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.data.Categoria

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
        val gridViewItem = inflater.inflate(R.layout.fragment_category_card, null)

        val textViewCategory = gridViewItem.findViewById<TextView>(R.id.txtCategoria)
        val imgViewCategory = gridViewItem.findViewById<ImageView>(R.id.imgComercio)

        textViewCategory.text = promocion.titulo
        imgViewCategory.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cat_default))
        // Utiliza la función base64ToBitmap para obtener el Bitmap del logotipo
        /*
        textViewCategory.text = comercio.nombre
        val logoBitmap = comercio.base64ToBitmap(comercio.logo)
        if (logoBitmap != null) {
            imgViewCategory.setImageBitmap(logoBitmap)
         }
         */
        // Agrega cualquier otra configuración específica de tu diseño aquí

        return gridViewItem
    }
}

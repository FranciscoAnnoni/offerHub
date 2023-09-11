import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.offerhub.Comercio
import com.example.offerhub.R
import com.example.offerhub.data.Categoria

class CategoryGridAdapter(private val context: Context, private val categorias: List<Categoria>) : BaseAdapter() {

    override fun getCount(): Int {
        return categorias.size
    }

    override fun getItem(position: Int): Any {
        return categorias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       val categoria = getItem(position) as Categoria
        // val comercio = getItem(position) as Comercio
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridViewItem = inflater.inflate(R.layout.fragment_category_card, null)

        val textViewCategory = gridViewItem.findViewById<TextView>(R.id.txtCategoria)
        val imgViewCategory = gridViewItem.findViewById<ImageView>(R.id.imgComercio)

        textViewCategory.text = categoria.nombre
        imgViewCategory.setImageDrawable(categoria.logo)
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

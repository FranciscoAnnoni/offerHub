import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.fragments.shopping.HomeFragment
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridAdapter(private val context: Context, private var promociones: List<Promocion>) : BaseAdapter() {

    private var areCheckBoxesVisible = false
    private var numCheckBoxesSeleccionados = 0
    public var lista:MutableList<Promocion> = mutableListOf()


    private lateinit var homeFragment: HomeFragment
    fun setHomeFragment(fragment: HomeFragment) {
        homeFragment = fragment
    }
    fun setCheckBoxesVisibility(shouldBeVisible: Boolean) {
        areCheckBoxesVisible = shouldBeVisible
        notifyDataSetChanged()
    }

    fun getSeleccion():MutableList<Promocion>{
        return lista
    }

    override fun getCount(): Int {
        return promociones.size
    }


    override fun getItem(position: Int): Any {
        return promociones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun actualizarDatos(nuevasPromociones: List<Promocion>) {
        promociones = nuevasPromociones
    }

    // ...
    fun setOnCheckedChangeListener(checkBox: CheckBox, position: Int) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                numCheckBoxesSeleccionados++
                lista.add(promociones[position])
                // Desmarcar los demás CheckBoxes
                if(numCheckBoxesSeleccionados==3){
                    homeFragment.updateButtonVisibility(false)
                }
            } else {
                lista.remove(promociones[position])
                numCheckBoxesSeleccionados--
                if (numCheckBoxesSeleccionados==1){
                    homeFragment.updateButtonVisibility(false)
                }
            }

            // Actualizar visibilidad del botón
            // asumiendo que 'btnActivar' es el botón que quieres mostrar
            if (numCheckBoxesSeleccionados==2){
                homeFragment.updateButtonVisibility(true)
            }

        }
    }
// ...


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val promocion = getItem(position) as Promocion

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridViewItem = inflater.inflate(R.layout.fragment_promocion_card, null)
        val instancia = Funciones()
        val checkBox: CheckBox = gridViewItem.findViewById(R.id.checkBox)
        checkBox.visibility = if (areCheckBoxesVisible) View.VISIBLE else View.GONE
        val tvComercio = gridViewItem.findViewById<TextView>(R.id.tvPromocionComercio)
        val tvDescuento = gridViewItem.findViewById<TextView>(R.id.tvPromocionDescuento)
        val favIcon = gridViewItem.findViewById<ImageView>(R.id.promoFav)
        val imgViewCategory = gridViewItem.findViewById<ImageView>(R.id.imgComercio)
        setOnCheckedChangeListener(checkBox, position)
        var textoPromo=promocion.obtenerDesc()
        if(promocion.tipoPromocion=="Reintegro" || promocion.tipoPromocion=="Descuento"){
            textoPromo=textoPromo+"%"
        }  else if (promocion.tipoPromocion=="Cuotas") {
            textoPromo = textoPromo + " cuotas"
        }
        tvDescuento.text = textoPromo

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val userViewModel = UserViewModelSingleton.getUserViewModel()
        coroutineScope.launch {
            tvComercio.text = instancia.traerInfoComercio(promocion.comercio, "nombre")
           /* var logo:Bitmap?=null
            if(userViewModel.logoComercios.containsKey(promocion.comercio)){
                logo = userViewModel.logoComercios[promocion.comercio]

            } else{
                val imgEnBase64=Funciones().traerLogoComercio(promocion.comercio)
                val imageByteArray = Base64.decode(imgEnBase64, Base64.DEFAULT)
                 logo = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                userViewModel.logoComercios[promocion.comercio!!]=logo!!
            }
            // Carga la imagen con Glide
            Glide.with(context)
                .load(logo)
                .placeholder(R.drawable.offerhub_logo_color) // Drawable de carga mientras se descarga la imagen
                .error(android.R.drawable.ic_dialog_alert) // Drawable de error si no se puede cargar la imagen
                .thumbnail(0.25f)
                .into(imgViewCategory)*/



        }


        val isFavorite = userViewModel.favoritos.any { it.id == promocion.id }
        favIcon.setImageResource(getFavResource(isFavorite))


        return gridViewItem
    }
}


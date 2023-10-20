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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.fragments.shopping.HomeFragment
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.interfaces.PromocionFragmentListener
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridAdapter(private val context: Context, private var promociones: List<Promocion>,private var fragmentListener: PromocionFragmentListener) : BaseAdapter() {

    private var currentPage = 1 // Página actual
    private val promocionesPorPagina = 25
    private var areCheckBoxesVisible = false
    private var numCheckBoxesSeleccionados = 0
    public var lista:MutableList<Promocion> = mutableListOf()
    private val elementoCache = HashMap<Int, View>()

    fun setFragmentListener(listener: PromocionFragmentListener) {
        fragmentListener = listener
    }

    fun vaciarCacheResultados(){
        elementoCache.clear()
    }

    private lateinit var fragment : Fragment
    fun eliminarLista(){
        lista.clear()
        numCheckBoxesSeleccionados = 0
    }
    fun setFragment(newFragment: Fragment) {
        fragment = newFragment
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

                if(numCheckBoxesSeleccionados==3){
                        numCheckBoxesSeleccionados--
                        lista.remove(promociones[position])
                        checkBox.isChecked=false
                        fragmentListener.mostrarAvisoSobreeleccion()
                }
            } else {
                lista.remove(promociones[position])
                numCheckBoxesSeleccionados--
                if (numCheckBoxesSeleccionados==1){
                    fragmentListener.updateButtonVisibility(false)
                }
            }

            // Actualizar visibilidad del botón
            // asumiendo que 'btnActivar' es el botón que quieres mostrar
            if (numCheckBoxesSeleccionados==2){
                fragmentListener.updateButtonVisibility(true)
            }

        }
    }
// ...


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val promocion = getItem(position) as Promocion

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridViewItem: View

        if (elementoCache.containsKey(position)) {
            gridViewItem = elementoCache[position]!! // Recupera desde la caché
        } else {
         gridViewItem = inflater.inflate(R.layout.fragment_promocion_card, null)
        val instancia = Funciones()
        val tvComercio = gridViewItem.findViewById<TextView>(R.id.tvPromocionComercio)
        val tvDescuento = gridViewItem.findViewById<TextView>(R.id.tvPromocionDescuento)
        val favIcon = gridViewItem.findViewById<ImageView>(R.id.promoFav)
        val imgViewCategory = gridViewItem.findViewById<ImageView>(R.id.imgComercio)
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
            var logo: Bitmap? = null
            if (userViewModel.logoComercios.containsKey(promocion.comercio)) {
                logo = userViewModel.logoComercios[promocion.comercio]
            } else {
                val imgEnBase64 = Funciones().traerLogoComercio(promocion.comercio)
                if (imgEnBase64 != null) {
                    val imageByteArray = Base64.decode(imgEnBase64, Base64.DEFAULT)
                    val width = 100 // Ancho deseado en píxeles
                    val height = 100 // Alto deseado en píxeles
                    var resizedBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, width, height, false)
                    logo = resizedBitmap
                    userViewModel.logoComercios[promocion.comercio!!] = logo
                } else {
                    // Tratar el caso en el que imgEnBase64 sea nulo
                }
            }

            // Carga la imagen con Glide después de obtener o redimensionarla
            Glide.with(context)
                .load(logo)
                .placeholder(R.drawable.offerhub_logo_color)
                .error(android.R.drawable.ic_dialog_alert)
                .thumbnail(0.25f)
                .into(imgViewCategory)
        }

        val isFavorite = userViewModel.favoritos.any { it.id == promocion.id }
        favIcon.setImageResource(getFavResource(isFavorite))

        if (position == promociones.size - 1) {
            cargarMasPromociones()
        }
            elementoCache[position] = gridViewItem
            }
        val checkBox: CheckBox = gridViewItem.findViewById(R.id.checkBox)
        checkBox.visibility = if (areCheckBoxesVisible) View.VISIBLE else View.GONE
        checkBox.isChecked=lista.any { it.id == promocion.id }
        setOnCheckedChangeListener(checkBox, position)
        return gridViewItem
    }


    private fun obtenerPromociones(page: Int, pageSize: Int): List<Promocion> {
        // Implementa la lógica para obtener más promociones desde tu fuente de datos
        // Utiliza 'page' y 'pageSize' para determinar qué conjunto de promociones cargar

        // Por ejemplo, si tus promociones están en una lista llamada "listaDePromociones":
        val startIndex = (page - 1) * pageSize
        val endIndex = startIndex + pageSize

        val promocionesCargadas = mutableListOf<Promocion>()

        if (startIndex < promociones.size) {
            for (i in startIndex until endIndex) {
                if (i < promociones.size) {
                    promocionesCargadas.add(promociones[i])
                }
            }
        }

        return promocionesCargadas
    }

    private fun cargarMasPromociones() {
        // Incrementa la página actual
        currentPage++

        // Obtén más promociones y agrégalas a la lista existente
        val nuevasPromociones = obtenerPromociones(currentPage, promocionesPorPagina)
        promociones += nuevasPromociones
        notifyDataSetChanged()
    }
}



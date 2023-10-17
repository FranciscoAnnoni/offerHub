import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.fragments.shopping.HomeFragment
import com.example.offerhub.fragments.shopping.HomeFragmentDirections
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridPorCategoriaAdapter(private val context: FragmentActivity, private val promociones: List<Promocion>, private val onItemClickListener: OnItemClickListener, private val onVerMasClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Tipos de elementos en el RecyclerView
    private val TIPO_PROMOCION = 1
    private val TIPO_VER_MAS = 2
    private var areCheckBoxesVisible = false
    private var numCheckBoxesSeleccionados = 0

    // Variable para controlar si se muestra el botón "Ver Más"
    private var mostrarMas = false
    private lateinit var homeFragment: HomeFragment
    public var lista:MutableList<Promocion> = mutableListOf()
    fun setHomeFragment(fragment: HomeFragment) {
        homeFragment = fragment
    }

    fun getSeleccion():MutableList<Promocion>{
        return lista
    }

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
    interface OnItemClickListener {
        fun onItemClick(promocion: Promocion)
        fun onVerMasClick()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIPO_PROMOCION -> {
                val view = LayoutInflater.from(context).inflate(R.layout.fragment_promocion_card, parent, false)
                PromocionViewHolder(view)

            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.fragment_ver_mas_card, parent, false)
                VerMasViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TIPO_PROMOCION -> {
                val promocion = promociones[position]
                val instancia = Funciones()
                val promocionViewHolder = holder as PromocionViewHolder
                promocionViewHolder.checkBox.visibility = if (areCheckBoxesVisible) View.VISIBLE else View.GONE
                setOnCheckedChangeListener(promocionViewHolder.checkBox, position)
                if(position==0){
                    val params = holder.layoutTarjetaPromo.layoutParams as ViewGroup.MarginLayoutParams
                    params.marginStart = 0
                    holder.layoutTarjetaPromo.layoutParams = params
                }
                val coroutineScope = CoroutineScope(Dispatchers.Main)

                coroutineScope.launch {
                    promocionViewHolder.textViewCategory.text = instancia.traerInfoComercio(promocion.comercio, "nombre")
                    val userViewModel = UserViewModelSingleton.getUserViewModel()
                    var textoPromo=promocion.obtenerDesc()
                    if(promocion.tipoPromocion=="Reintegro" || promocion.tipoPromocion=="Descuento"){
                        textoPromo=textoPromo+"%"
                    }  else if (promocion.tipoPromocion=="Cuotas") {
                        textoPromo = textoPromo + " cuotas"
                    }
                    promocionViewHolder.textViewDto.text = textoPromo
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
                        .into(promocionViewHolder.imgViewCategory)

                    /*
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
                        .into(promocionViewHolder.imgViewCategory)*/

                    val isFavorite = userViewModel.favoritos.any{ it.id == promocion.id }
                    promocionViewHolder.favIcon.setImageResource(getFavResource(isFavorite))
                }
                // Configura más vistas para mostrar detalles adicionales de la promoción si es necesario
            }
            TIPO_VER_MAS -> {
                // Configurar el botón "Ver Más" si es necesario

            }
        }
    }

    override fun getItemCount(): Int {
        // Si se muestra el botón "Ver Más", agrega 1 al recuento total
        return if (mostrarMas) {
            promociones.size + 1
        } else {
            promociones.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Determina el tipo de elemento en función de la posición
        return if (position == 10 && !mostrarMas) {
            TIPO_VER_MAS
        } else {
            TIPO_PROMOCION
        }
    }

    // Método para mostrar u ocultar el botón "Ver Más"
    fun mostrarBotonVerMas(mostrar: Boolean) {
        mostrarMas = mostrar
        notifyDataSetChanged()
    }

    inner class PromocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val textViewCategory = itemView.findViewById<TextView>(R.id.tvPromocionComercio)
        val textViewDto = itemView.findViewById<TextView>(R.id.tvPromocionDescuento)
        val favIcon = itemView.findViewById<ImageView>(R.id.promoFav)
        val imgViewCategory = itemView.findViewById<ImageView>(R.id.imgComercio)
        val layoutTarjetaPromo = itemView.findViewById<ConstraintLayout>(R.id.layoutTarjetaPromo)
        // Agrega más vistas aquí para mostrar detalles adicionales de la promoción si es necesario
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val promocion = promociones[position]
                    onItemClickListener.onItemClick(promocion)
                }
            }
        }
    }

    inner class VerMasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnVerMas = itemView.findViewById<ConstraintLayout>(R.id.btnVerMas)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVerMasClickListener.onVerMasClick()
                }
            }
        }
    }

}

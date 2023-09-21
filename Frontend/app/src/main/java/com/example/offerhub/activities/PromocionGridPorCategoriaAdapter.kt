import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.offerhub.Comercio
import com.example.offerhub.Funciones
import com.example.offerhub.Promocion
import com.example.offerhub.R
import com.example.offerhub.funciones.getFavResource
import com.example.offerhub.viewmodel.UserViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PromocionGridPorCategoriaAdapter(private val context: Context, private val promociones: List<Promocion>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Tipos de elementos en el RecyclerView
    private val TIPO_PROMOCION = 1
    private val TIPO_VER_MAS = 2

    // Variable para controlar si se muestra el botón "Ver Más"
    private var mostrarMas = false

    interface OnItemClickListener {
        fun onItemClick(promocion: Promocion)
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
                if(position==0){
                    val params = holder.layoutTarjetaPromo.layoutParams as ViewGroup.MarginLayoutParams
                    params.marginStart = 0
                    holder.layoutTarjetaPromo.layoutParams = params
                }
                val coroutineScope = CoroutineScope(Dispatchers.Main)

                coroutineScope.launch {
                    promocionViewHolder.textViewCategory.text = instancia.traerInfoComercio(promocion.comercio, "nombre")

                    val userViewModel = UserViewModelSingleton.getUserViewModel()
                    var logo: Bitmap?=null

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
                val verMasViewHolder = holder as VerMasViewHolder
                verMasViewHolder.btnVerMas.setOnClickListener {
                    // Maneja la navegación al otro fragmento aquí
                    // Por ejemplo, puedes usar Navigation Component para navegar
                    // a otro fragmento al hacer clic en "Ver Más"
                }
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
        val textViewCategory = itemView.findViewById<TextView>(R.id.tvPromocionComercio)
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
    }

}

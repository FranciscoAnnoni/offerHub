import android.view.View
import androidx.lifecycle.ViewModel
import com.example.offerhub.LecturaBD
import com.example.offerhub.Promocion
import com.example.offerhub.funciones.removeAccents
import com.example.offerhub.interfaces.FilterData
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel: ViewModel() {
    var promociones:MutableList<Promocion> = mutableListOf()
    public var filtrosActuales: FilterData? = null
    public var textoBusqueda: String? = ""

    // Método para obtener los filtros actuales
    fun obtenerFiltrosActuales(): FilterData? {
        return filtrosActuales
    }
    fun buscarPorTexto(palabra:String): Job {
        if(palabra.length>0) {
            textoBusqueda=palabra
            return CoroutineScope(Dispatchers.Main).launch {
                val porTextPromociones= LecturaBD().filtrarPromos(
                    listOf(
                        "titulo" to palabra,
                        "categoria" to palabra,
                    ), false
                )
                if(porTextPromociones.size>0){
                    promociones=porTextPromociones as MutableList<Promocion>
                } else {
                    promociones= mutableListOf()
                }
            }
        }
        return CoroutineScope(Dispatchers.Main).launch {}
    }

    fun buscarPorCategoria(nombre:String): Job {

        return CoroutineScope(Dispatchers.Main).launch {
            textoBusqueda=nombre
             val porCatPromociones = LecturaBD().filtrarPromos(listOf(
                 "categoria" to nombre,
             ),false)
            if(porCatPromociones.isNotEmpty()){
                promociones=porCatPromociones as MutableList<Promocion>
            } else {
                promociones= mutableListOf()
            }

        }
    }

    fun filtrarPorVigencia(desde: String?, hasta: String?): List<Promocion> {
        if (desde.isNullOrEmpty() && hasta.isNullOrEmpty()) {
            // Si ambos campos de fecha están vacíos, no aplicamos el filtro
            return promociones
        }

        return promociones.filter { promocion ->
            val fechaInicio = promocion.vigenciaDesde // Supongamos que esta propiedad contiene la fecha de inicio de la promoción
            val fechaFin = promocion.vigenciaHasta // Supongamos que esta propiedad contiene la fecha de fin de la promoción

            // Verificamos si la fecha de inicio es después o igual a "desde" (si se especificó) y
            // si la fecha de fin es antes o igual a "hasta" (si se especificó)
            (desde.isNullOrEmpty() || fechaInicio.toString() >= desde) &&
                    (hasta.isNullOrEmpty() || fechaFin.toString() <= hasta)
        }
    }

    fun filtrarPorTipoPromocion(tiposSeleccionados: List<String>): List<Promocion> {
        if (tiposSeleccionados.isEmpty()) {
            // Si no se seleccionó ningún tipo de promoción, no aplicamos el filtro
            return promociones
        }

        return promociones.filter { promocion ->
            val tipoPromocion = promocion.tipoPromocion // Supongamos que esta propiedad contiene el tipo de promoción

            // Verificamos si el tipo de promoción está en la lista de tipos seleccionados
            removeAccents(tipoPromocion) in tiposSeleccionados.map { it -> removeAccents(it) }
        }
    }

    fun filtrarPorDiasPromocion(diasSeleccionados: List<String>): List<Promocion> {
        if (diasSeleccionados.isEmpty()) {
            // Si no se seleccionó ningún día de promoción, no aplicamos el filtro
            return promociones
        }

        return promociones.filter { promocion ->
            val diasPromocion = promocion.dias // Supongamos que esta propiedad contiene los días de promoción

            // Verificamos si al menos uno de los días de promoción está en la lista de días seleccionados
            diasPromocion?.any { removeAccents( it) in diasSeleccionados.map { it -> removeAccents(it) } } ?: true
        }
    }




    fun actualizarFiltrosActuales(filters: FilterData) {
        filtrosActuales = filters
    }



}


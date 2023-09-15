import androidx.lifecycle.ViewModel
import com.example.offerhub.Promocion

class UserViewModel : ViewModel() {
    var id: String?=null
    var listadoDePromosDisp: List<Promocion> = emptyList()
    var favoritos: MutableList<Promocion> = mutableListOf()
    var homeModoFull: String? = null
}

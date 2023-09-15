import androidx.lifecycle.ViewModel
import com.example.offerhub.Promocion

class UserViewModel : ViewModel() {
    var id: String?=null
    var listadoDePromosDisp: List<Promocion> = emptyList()
    var favoritos: List<String> = emptyList()
    var homeModoFull: String? = null
}

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.offerhub.Promocion
import com.example.offerhub.Usuario
import com.example.offerhub.data.UserPartner
import javax.inject.Singleton

class UserViewModel : ViewModel() {
    var id: String?=null
    var listadoDePromosDisp: List<Promocion> = emptyList()
    var favoritos: MutableList<Promocion> = mutableListOf()
    var reintegros: MutableList<Promocion> = mutableListOf()
    var logoComercios: MutableMap<String,Bitmap> = mutableMapOf()
    var usuario: Usuario? = null
    var usuarioPartner: UserPartner? = null
    var homeModoFull: String? = null

}

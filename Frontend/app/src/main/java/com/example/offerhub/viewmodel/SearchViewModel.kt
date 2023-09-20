import androidx.lifecycle.ViewModel
import com.example.offerhub.Promocion
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchViewModel: ViewModel() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("promotions")

        suspend fun searchPromotions(searchQuery: String): List<Promocion> {
        return withContext(Dispatchers.IO) {
            val query: Query = databaseReference.orderByChild("nombre")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")

            val result = mutableListOf<Promocion>()

            try {
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val promotion = snapshot.getValue(Promocion::class.java)
                            if (promotion != null) {
                                result.add(promotion)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejar errores, si los hay
                    }
                })
            } catch (e: Exception) {
                // Manejar excepciones
                println("Error al buscar promociones: ${e.message}")
            }

            return@withContext result
        }
    }
}


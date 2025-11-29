package mx.com.virtualhand.invex.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mx.com.virtualhand.invex.domain.Categoria

class CategoryRepository(instance: FirebaseFirestore) {

    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesRef = firestore.collection("categorias")

    /** AGREGAR O ACTUALIZAR */
    fun addOrUpdateCategory(cat: Categoria, onComplete: (Boolean) -> Unit) {
        val docId = if (cat.id.isEmpty()) categoriesRef.document().id else cat.id
        val data = cat.copy(id = docId)

        categoriesRef.document(docId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /** OBTENER EN TIEMPO REAL */
    fun getCategoriesRealtime(): Flow<List<Categoria>> = callbackFlow {
        val listener: ListenerRegistration = categoriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val list = snapshot?.documents?.mapNotNull { it.toObject(Categoria::class.java) } ?: emptyList()
            trySend(list)
        }

        awaitClose { listener.remove() }
    }

    /** ELIMINAR */
    fun deleteCategory(id: String, onComplete: (Boolean) -> Unit) {
        categoriesRef.document(id)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}


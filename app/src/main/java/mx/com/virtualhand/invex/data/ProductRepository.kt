package mx.com.virtualhand.invex.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import mx.com.virtualhand.invex.domain.Product

class ProductRepository(private val db: FirebaseFirestore) {

    private val collection = db.collection("products")

    fun addOrUpdateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        val docRef = if (product.id.isEmpty()) collection.document() else collection.document(product.id)
        docRef.set(product.copy(id = docRef.id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getProductsRealtime() = callbackFlow<List<Product>> {
        val listener: ListenerRegistration = collection.addSnapshotListener { snapshot, _ ->
            val list = snapshot?.toObjects(Product::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun deleteProduct(id: String, onComplete: (Boolean) -> Unit) {
        collection.document(id)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}

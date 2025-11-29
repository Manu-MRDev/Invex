package mx.com.virtualhand.invex.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import mx.com.virtualhand.invex.domain.Movimiento
import mx.com.virtualhand.invex.domain.Product

class MovementRepository(private val db: FirebaseFirestore) {

    private val movimientosCollection = db.collection("movimientos")
    private val productsCollection = db.collection("products")

    /**
     * Agrega un movimiento y actualiza stock del producto en una TRANSACTION.
     * Si tipo == "Entrada" suma, si "Salida" resta (y valida que no quede negativo).
     */
    fun addMovementAndUpdateStock(movimiento: Movimiento, onComplete: (Boolean, String?) -> Unit) {
        val prodRef = productsCollection.document(movimiento.productId)
        val movRef = movimientosCollection.document() // nuevo id

        db.runTransaction { transaction ->
            val prodSnap = transaction.get(prodRef)
            val prod = prodSnap.toObject(Product::class.java)
                ?: throw Exception("Producto no encontrado")

            val currentStock = prod.stock
            val delta = movimiento.cantidad
            val newStock = if (movimiento.tipo == "Entrada") currentStock + delta else currentStock - delta

            if (newStock < 0) throw Exception("Stock insuficiente")

            // actualizar producto (manteniendo otros campos)
            val updatedProduct = prod.copy(stock = newStock, id = prodRef.id)
            transaction.set(prodRef, updatedProduct, SetOptions.merge())

            // guardar movimiento (con id y fecha)
            val movToSave = movimiento.copy(id = movRef.id, fecha = System.currentTimeMillis(), productName = prod.nombre)
            transaction.set(movRef, movToSave, SetOptions.merge())

            // retorna cualquier valor (no usado)
            null
        }.addOnSuccessListener {
            onComplete(true, null)
        }.addOnFailureListener { ex ->
            onComplete(false, ex.message)
        }
    }

    /** Stream realtime de movimientos, ordenado por fecha descendente */
    fun getMovementsRealtime() = callbackFlow<List<Movimiento>> {
        val listener: ListenerRegistration = movimientosCollection
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Movimiento::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}

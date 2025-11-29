package mx.com.virtualhand.invex.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mx.com.virtualhand.invex.domain.Categoria
import mx.com.virtualhand.invex.domain.Movimiento
import mx.com.virtualhand.invex.domain.Product

class ReportRepository(
    private val db: FirebaseFirestore
) {

    fun listenProducts(): Flow<List<Product>> = callbackFlow {
        val sub = db.collection("products")
            .addSnapshotListener { snap, _ ->
                if (snap != null) trySend(snap.toObjects(Product::class.java))
            }
        awaitClose { sub.remove() }
    }

    fun listenCategories(): Flow<List<Categoria>> = callbackFlow {
        val sub = db.collection("categorias") // 🔹 nombre exacto de la colección
            .addSnapshotListener { snap, _ ->
                if (snap != null) trySend(snap.toObjects(Categoria::class.java))
            }
        awaitClose { sub.remove() }
    }

    fun listenMovements(): Flow<List<Movimiento>> = callbackFlow {
        val sub = db.collection("movimientos") // 🔹 nombre exacto de la colección
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snap, _ ->
                if (snap != null) trySend(snap.toObjects(Movimiento::class.java))
            }
        awaitClose { sub.remove() }
    }
}

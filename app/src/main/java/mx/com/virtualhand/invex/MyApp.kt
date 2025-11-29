package mx.com.virtualhand.invex

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this) // Inicializa Firebase

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Cache local habilitado
            .build()

        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
    }
}

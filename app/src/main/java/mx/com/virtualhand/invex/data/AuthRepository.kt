package mx.com.virtualhand.invex.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository(
    private val auth: FirebaseAuth
) {

    // Iniciar sesión con Google
    fun signInWithGoogle(account: GoogleSignInAccount): Task<AuthResult> {
        val credential: AuthCredential =
            GoogleAuthProvider.getCredential(account.idToken, null)

        return auth.signInWithCredential(credential)
    }

    // Obtener usuario actual
    fun getCurrentUser() = auth.currentUser

    // Cerrar sesión
    fun logout() {
        auth.signOut()
    }
}

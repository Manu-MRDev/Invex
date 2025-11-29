package mx.com.virtualhand.invex.domain

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import mx.com.virtualhand.invex.data.AuthRepository

class AuthUseCase(
    private val repository: AuthRepository
) {

    // Login con Google
    fun loginWithGoogle(account: GoogleSignInAccount): Task<AuthResult> {
        return repository.signInWithGoogle(account)
    }

    // Usuario actual
    fun getCurrentUser() = repository.getCurrentUser()

    // Cerrar sesión
    fun logout() {
        repository.logout()
    }
}

package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mx.com.virtualhand.invex.domain.AuthUseCase
import mx.com.virtualhand.invex.ui.screens.LoginUiState

class LoginViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        // Comprobar si ya hay usuario logueado
        if (authUseCase.getCurrentUser() != null) {
            _uiState.value = _uiState.value.copy(isLoggedIn = true)
        }
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                authUseCase.loginWithGoogle(account).await()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    error = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }

    fun logout() {
        authUseCase.logout()
        _uiState.value = _uiState.value.copy(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.com.virtualhand.invex.domain.AuthUseCase

class LoginViewModelFactory(
    private val authUseCase: AuthUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(authUseCase) as T
    }
}

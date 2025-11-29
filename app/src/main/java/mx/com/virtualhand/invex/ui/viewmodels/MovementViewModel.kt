package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.com.virtualhand.invex.data.MovementRepository
import mx.com.virtualhand.invex.domain.Movimiento

class MovementViewModel(private val repo: MovementRepository) : ViewModel() {

    private val _movements = MutableStateFlow<List<Movimiento>>(emptyList())
    val movements: StateFlow<List<Movimiento>> = _movements.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    init {
        viewModelScope.launch {
            _loading.value = true
            repo.getMovementsRealtime().collect { list ->
                _movements.value = list
                _loading.value = false
            }
        }
    }

    /**
     * Guarda movimiento y actualiza stock dentro de repo (transaction).
     * callback recibe (success, message)
     */
    fun saveMovement(mov: Movimiento, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            repo.addMovementAndUpdateStock(mov) { success, msg ->
                _loading.value = false
                if (!success) {
                    viewModelScope.launch { _error.emit(msg ?: "Error al guardar movimiento") }
                }
                callback(success, msg)
            }
        }
    }

}

class MovementViewModelFactory(private val repo: MovementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovementViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

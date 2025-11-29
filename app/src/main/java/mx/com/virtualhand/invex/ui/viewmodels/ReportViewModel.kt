package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mx.com.virtualhand.invex.data.ReportRepository
import mx.com.virtualhand.invex.ui.screens.ReportUiState

class ReportViewModel(
    private val repo: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> get() = _uiState

    init {
        cargarDatosTiempoReal()
    }

    private fun cargarDatosTiempoReal() {
        viewModelScope.launch {
            combine(
                repo.listenProducts(),
                repo.listenCategories(),
                repo.listenMovements()
            ) { productos, categorias, movimientos ->

                ReportUiState(
                    isLoading = false,
                    products = productos,
                    categories = categorias,
                    movements = movimientos
                )

            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }
}

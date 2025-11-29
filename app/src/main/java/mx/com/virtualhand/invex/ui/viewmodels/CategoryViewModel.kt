package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mx.com.virtualhand.invex.domain.Categoria
import mx.com.virtualhand.invex.data.CategoryRepository

class CategoryViewModel(
    private val repo: CategoryRepository
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    init {
        loadRealtime()
    }

    /** ESCUCHAR CAMBIOS EN TIEMPO REAL */
    private fun loadRealtime() {
        viewModelScope.launch {
            repo.getCategoriesRealtime().collectLatest { list ->
                _categorias.value = list
            }
        }
    }

    /** GUARDAR */
    fun saveCategory(cat: Categoria) {
        repo.addOrUpdateCategory(cat) { success ->
            // No hace falta recargar, realtime ya actualiza solo
        }
    }

    /** ELIMINAR */
    fun deleteCategory(id: String) {
        repo.deleteCategory(id) { success ->
            // Tampoco hace falta recargar
        }
    }
}



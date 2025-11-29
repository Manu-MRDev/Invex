package mx.com.virtualhand.invex.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.com.virtualhand.invex.domain.Product
import mx.com.virtualhand.invex.domain.ProductUseCase

class ProductViewModel(private val useCase: ProductUseCase) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    init {
        viewModelScope.launch {
            useCase.getProductsRealtime().collect {
                _products.value = it
            }
        }
    }

    fun saveProduct(product: Product) {
        useCase.addOrUpdateProduct(product) { success ->
            // Aquí podrías mostrar un snackbar si necesitas
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            useCase.deleteProduct(id) { success ->
                // Aquí puedes poner logs o snackbar
            }
        }
    }


}


package mx.com.virtualhand.invex.domain

import kotlinx.coroutines.flow.Flow
import mx.com.virtualhand.invex.data.ProductRepository

class ProductUseCase(private val repository: ProductRepository) {

    fun addOrUpdateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        repository.addOrUpdateProduct(product, onComplete)
    }

    fun getProductsRealtime(): Flow<List<Product>> {
        return repository.getProductsRealtime()
    }

    fun deleteProduct(id: String, onComplete: (Boolean) -> Unit) {
        repository.deleteProduct(id, onComplete)
    }

}
import mx.com.virtualhand.invex.domain.Categoria

import kotlinx.coroutines.flow.Flow
import mx.com.virtualhand.invex.data.CategoryRepository

class CategoryUseCase(private val repo: CategoryRepository) {

    fun addOrUpdateCategory(cat: Categoria, onComplete: (Boolean) -> Unit) {
        repo.addOrUpdateCategory(cat, onComplete)
    }

    fun getCategoriesRealtime(): Flow<List<Categoria>> {
        return repo.getCategoriesRealtime()
    }

    fun deleteCategory(id: String, onComplete: (Boolean) -> Unit) {
        repo.deleteCategory(id, onComplete)
    }
}


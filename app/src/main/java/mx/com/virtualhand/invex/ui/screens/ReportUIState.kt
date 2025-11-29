package mx.com.virtualhand.invex.ui.screens

import mx.com.virtualhand.invex.domain.*

data class ReportUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val categories: List<Categoria> = emptyList(),
    val movements: List<Movimiento> = emptyList()
) {
    val totalProductos get() = products.size
    val totalCategorias get() = categories.size
    val totalStock get() = products.sumOf { it.stock }
    val valorTotal get() = products.sumOf { it.stock * it.precio }

    val productosPocoStock get() = products.filter { it.stock <= 5 }

    // 🔹 Valor por categoría corregido
    val valorPorCategoria get() = categories.map { cat ->
        val suma = products.filter { it.categoriaId == cat.id }
            .sumOf { it.stock * it.precio }
        cat.nombre to suma
    }

    // 🔹 Movimientos recientes ordenados por fecha descendente y máximo 10
    val movimientosRecientes get() = movements
        .sortedByDescending { it.fecha }
        .take(10)
}

package mx.com.virtualhand.invex.domain

data class Product(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val descripcion: String = "",
    val categoriaId: String = ""   // 🔥 relación con categoría
)




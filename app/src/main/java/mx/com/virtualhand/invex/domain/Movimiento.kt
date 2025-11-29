package mx.com.virtualhand.invex.domain

data class Movimiento(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val tipo: String = "", // Entrada o Salida
    val cantidad: Int = 0,
    val fecha: Long = System.currentTimeMillis()
)

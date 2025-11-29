package mx.com.virtualhand.invex.domain

import mx.com.virtualhand.invex.data.MovementRepository
import mx.com.virtualhand.invex.domain.Movimiento

class MovementUseCase(private val repository: MovementRepository) {

    fun addMovement(movimiento: Movimiento, onComplete: (Boolean, String?) -> Unit) {
        repository.addMovementAndUpdateStock(movimiento, onComplete)
    }

    fun getMovementsRealtime() = repository.getMovementsRealtime()
}


package mx.com.virtualhand.invex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.invex.ui.viewmodels.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ReportContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ReportContent(state: ReportUiState, modifier: Modifier = Modifier) {
    LazyColumn(modifier.padding(16.dp)) {

        item { ResumenGeneral(state) }

        item { Spacer(Modifier.height(16.dp)) }
        item { ProductosPocoStock(state) }

        item { Spacer(Modifier.height(16.dp)) }
        item { ValorPorCategoria(state) }

        item { Spacer(Modifier.height(16.dp)) }
        item { MovimientosRecientes(state) }
    }
}

@Composable
fun ResumenGeneral(state: ReportUiState) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Resumen General", style = MaterialTheme.typography.titleMedium)

            Text("Total de productos: ${state.totalProductos}")
            Text("Total de categorías: ${state.totalCategorias}")
            Text("Unidades en stock: ${state.totalStock}")
            Text("Valor total del inventario: $${state.valorTotal}")
        }
    }
}

@Composable
fun ProductosPocoStock(state: ReportUiState) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Productos con poco stock", style = MaterialTheme.typography.titleMedium)

            if (state.productosPocoStock.isEmpty()) {
                Text("No hay productos con stock bajo.")
            } else {
                state.productosPocoStock.forEach { p ->
                    Text("${p.nombre} • Stock: ${p.stock}")
                }
            }
        }
    }
}

@Composable
fun ValorPorCategoria(state: ReportUiState) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Valor por categoría", style = MaterialTheme.typography.titleMedium)

            state.valorPorCategoria.forEach { (categoria, valor) ->
                Text("$categoria → $$valor")
            }
        }
    }
}

@Composable
fun MovimientosRecientes(state: ReportUiState) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Movimientos recientes", style = MaterialTheme.typography.titleMedium)

            if (state.movimientosRecientes.isEmpty()) {
                Text("No hay movimientos recientes.")
            } else {
                state.movimientosRecientes.forEach { m ->
                    val nombre = if (m.productName.isNotEmpty()) m.productName else m.productId
                    val color = if (m.tipo.equals("Entrada", true)) Color.Green else Color.Red
                    Text("$nombre • ${m.tipo} • Cantidad: ${m.cantidad}", color = color)
                }
            }
        }
    }
}


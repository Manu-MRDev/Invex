package mx.com.virtualhand.invex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.com.virtualhand.invex.domain.Movimiento
import mx.com.virtualhand.invex.domain.Product
import mx.com.virtualhand.invex.ui.viewmodels.MovementViewModel
import mx.com.virtualhand.invex.ui.viewmodels.ProductViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    onBack: () -> Unit,
    movementViewModel: MovementViewModel,
    productViewModel: ProductViewModel
) {
    val movements by movementViewModel.movements.collectAsState()
    val loading by movementViewModel.loading.collectAsState()
    val products by productViewModel.products.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // <- CoroutineScope para mostrar snackbar

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (loading && movements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Box
            }

            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    items(movements) { mov ->
                        MovementCard(mov)
                    }
                }
            }

            if (showForm) {
                MovementFormModal(
                    products = products,
                    onCancel = { showForm = false },
                    onSave = { mov ->
                        movementViewModel.saveMovement(mov) { success, msg ->
                            if (success) {
                                showForm = false
                            } else {
                                val text = msg ?: "Error al guardar movimiento"
                                scope.launch {
                                    snackbarHostState.showSnackbar(text)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MovementCard(mov: Movimiento) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(mov.fecha))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(mov.productName, style = MaterialTheme.typography.titleMedium)
            Text("Tipo: ${mov.tipo}")
            Text("Cantidad: ${mov.cantidad}")
            Text("Fecha: $fecha")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementFormModal(
    products: List<Product>,
    onCancel: () -> Unit,
    onSave: (Movimiento) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var productText by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Entrada") }
    var cantidad by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }

    val filteredProducts = products.filter { it.nombre.contains(productText, ignoreCase = true) }

    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Registrar Movimiento", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            // Dropdown Autocomplete Producto
            ExposedDropdownMenuBox(
                expanded = showDropdown,
                onExpandedChange = { showDropdown = !showDropdown }
            ) {
                OutlinedTextField(
                    value = productText,
                    onValueChange = { value ->
                        productText = value
                        showDropdown = true
                        selectedProduct = null
                    },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = showDropdown && filteredProducts.isNotEmpty(),
                    onDismissRequest = { showDropdown = false }
                ) {
                    filteredProducts.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.nombre) },
                            onClick = {
                                selectedProduct = p
                                productText = p.nombre
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tipo: Entrada o Salida
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(selected = tipo == "Entrada", onClick = { tipo = "Entrada" }, label = { Text("Entrada") })
                FilterChip(selected = tipo == "Salida", onClick = { tipo = "Salida" }, label = { Text("Salida") })
            }

            Spacer(Modifier.height(12.dp))

            // Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it.filter { ch -> ch.isDigit() } },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(20.dp))

            // Botones
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onCancel) { Text("Cancelar") }
                Button(onClick = {
                    val qty = cantidad.toIntOrNull() ?: 0
                    if (selectedProduct == null) return@Button
                    if (qty <= 0) return@Button
                    val mov = Movimiento(
                        productId = selectedProduct!!.id,
                        productName = selectedProduct!!.nombre,
                        tipo = tipo,
                        cantidad = qty
                    )
                    onSave(mov)
                }) { Text("Guardar") }
            }
        }
    }
}

package mx.com.virtualhand.invex.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.com.virtualhand.invex.domain.Product
import mx.com.virtualhand.invex.ui.viewmodels.CategoryViewModel
import mx.com.virtualhand.invex.ui.viewmodels.ProductViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.ArrowDropDown


// ========================================================================
//  PANTALLA PRINCIPAL PRODUCTS
// ========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onBack: (() -> Unit)? = null,
    productViewModel: ProductViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var currentProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Button(
                onClick = {
                    currentProduct = null
                    showForm = true
                }
            ) {
                Text("Agregar Producto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = {
                            currentProduct = it
                            showForm = true
                        },
                        onDelete = { p ->
                            productViewModel.deleteProduct(p.id)
                        }
                    )
                    Divider()
                }
            }
        }

        if (showForm) {
            ProductForm(
                product = currentProduct,
                onSave = { saved ->
                    productViewModel.saveProduct(saved)
                    showForm = false
                    currentProduct = null
                },
                onCancel = {
                    showForm = false
                    currentProduct = null
                }
            )
        }
    }
}


// ========================================================================
//  ITEM DE PRODUCTO
// ========================================================================

@Composable
fun ProductItem(
    product: Product,
    onClick: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick(product) }
        ) {
            Text(product.nombre, style = MaterialTheme.typography.titleMedium)
            Text("Precio: ${product.precio}")
            Text("Stock: ${product.stock}")
            Text("Descripción: ${product.descripcion}")
        }

        IconButton(onClick = { onDelete(product) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}


// ========================================================================
//  FORMULARIO COMPLETO (AGREGAR / EDITAR)
// ========================================================================

@Composable
fun ProductForm(
    product: Product?,
    onSave: (Product) -> Unit,
    onCancel: () -> Unit,
    categoryViewModel: CategoryViewModel = viewModel()
) {
    // ❗ CORREGIDO: el StateFlow se llama "categorias"
    val categories by categoryViewModel.categorias.collectAsState()

    var nombre by remember { mutableStateOf(product?.nombre ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(product?.descripcion ?: "") }

    var categoriaId by remember { mutableStateOf(product?.categoriaId ?: "") }

    var expanded by remember { mutableStateOf(false) }

    // Obtener el nombre de la categoría seleccionada
    val categoriaSeleccionada =
        categories.firstOrNull { it.id == categoriaId }?.nombre ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 6.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = if (product == null) "Agregar Producto" else "Editar Producto",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(Modifier.height(16.dp))

                // --------------------------------------------------------------------
                //              SELECTOR DE CATEGORÍA
                // --------------------------------------------------------------------
                Text("Categoría", style = MaterialTheme.typography.titleMedium)

                Box {
                    OutlinedTextField(
                        value = categoriaSeleccionada.ifEmpty { "Selecciona una categoría" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nombre) },
                                onClick = {
                                    categoriaId = cat.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (nombre.isNotEmpty() && categoriaId.isNotEmpty()) {
                                onSave(
                                    Product(
                                        id = product?.id ?: "",
                                        nombre = nombre,
                                        precio = precio.toDoubleOrNull() ?: 0.0,
                                        stock = stock.toIntOrNull() ?: 0,
                                        descripcion = descripcion,
                                        categoriaId = categoriaId
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}


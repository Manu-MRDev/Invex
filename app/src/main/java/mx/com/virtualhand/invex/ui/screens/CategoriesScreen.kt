package mx.com.virtualhand.invex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.invex.domain.Categoria
import mx.com.virtualhand.invex.ui.viewmodels.CategoryViewModel
import mx.com.virtualhand.invex.ui.viewmodels.ProductViewModel


// ===================================================================
//                     CATEGORIES SCREEN COMPLETA
// ===================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categoryVM: CategoryViewModel,
    productVM: ProductViewModel,
    onBack: (() -> Unit)? = null
) {
    val categories by categoryVM.categorias.collectAsState()
    val products by productVM.products.collectAsState()

    // --- Estados de la pantalla ---
    var showForm by remember { mutableStateOf(false) }
    var currentCategory by remember { mutableStateOf<Categoria?>(null) }

    var detailCategory by remember { mutableStateOf<Categoria?>(null) }
    var editDetail by remember { mutableStateOf(false) }

    // ================================================================
    //     SI detailCategory != null → MOSTRAR "DETALLE" DENTRO MISMO
    // ================================================================
    if (detailCategory != null) {

        val cat = detailCategory!!
        val productos = products.filter { it.categoriaId == cat.id }
        val valorTotal = productos.sumOf { it.precio * it.stock }

        // ---------- MODO EDICIÓN DE DETALLES ----------
        var nombre by remember { mutableStateOf(cat.nombre) }
        var descripcion by remember { mutableStateOf(cat.descripcion) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles de ${cat.nombre}") },
                    navigationIcon = {
                        IconButton(onClick = {
                            detailCategory = null
                            editDetail = false
                        }) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier.padding(padding).padding(16.dp)
            ) {

                if (!editDetail) {

                    Text("Descripción: ${cat.descripcion}")
                    Text("Productos: ${productos.size}")
                    Text("Valor del inventario: $${"%.2f".format(valorTotal)}")

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { editDetail = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Editar categoría") }

                    Spacer(Modifier.height(20.dp))
                    Divider()
                    Spacer(Modifier.height(16.dp))

                    Text("Productos:", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    productos.forEach { p ->
                        Text("• ${p.nombre}")
                    }

                } else {

                    // -------- FORMULARIO DE EDICIÓN --------
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            categoryVM.saveCategory(
                                cat.copy(
                                    nombre = nombre,
                                    descripcion = descripcion
                                )
                            )
                            editDetail = false
                            detailCategory = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }
                }
            }
        }

        return  // ← evita que se dibuje la lista debajo
    }

    // ================================================================
    //                      VISTA NORMAL (lista)
    // ================================================================

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.Default.Close, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Button(onClick = {
                currentCategory = null
                showForm = true
            }) {
                Text("Agregar Categoría")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(categories) { cat ->

                    var expanded by remember { mutableStateOf(false) }

                    val productos = products.filter { it.categoriaId == cat.id }
                    val valorTotal = productos.sumOf { it.precio * it.stock }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { expanded = !expanded },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(cat.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(cat.descripcion)

                                    Spacer(Modifier.height(6.dp))

                                    Text("Productos: ${productos.size}")
                                    Text("Valor total: $${"%.2f".format(valorTotal)}")
                                }

                                Column {
                                    IconButton(onClick = {
                                        currentCategory = cat
                                        showForm = true
                                    }) {
                                        Icon(Icons.Default.Close, "Editar")
                                    }

                                    IconButton(onClick = {
                                        categoryVM.deleteCategory(cat.id)
                                    }) {
                                        Icon(Icons.Default.Delete, "Eliminar")
                                    }
                                }
                            }

                            // ------- EXPANDIBLE -------
                            AnimatedVisibility(
                                visible = expanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {

                                    Divider()
                                    Spacer(Modifier.height(8.dp))

                                    Text("Productos:", style = MaterialTheme.typography.titleSmall)
                                    Spacer(Modifier.height(4.dp))

                                    productos.forEach { p ->
                                        Text("• ${p.nombre}")
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Button(
                                        onClick = { detailCategory = cat },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Ver detalles")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ===================== FORMULARIO FLOTANTE =====================
        if (showForm) {
            CategoryForm(
                category = currentCategory,
                onSave = {
                    categoryVM.saveCategory(it)
                    showForm = false
                },
                onCancel = { showForm = false }
            )
        }
    }
}


// ===================================================================
//                     CATEGORY FORM (MISMO ARCHIVO)
// ===================================================================

@Composable
fun CategoryForm(
    category: Categoria?,
    onSave: (Categoria) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf(category?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(category?.descripcion ?: "") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            tonalElevation = 6.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    if (category == null) "Agregar Categoría" else "Editar Categoría",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (nombre.isNotEmpty()) {
                                onSave(
                                    Categoria(
                                        id = category?.id ?: "",
                                        nombre = nombre,
                                        descripcion = descripcion
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

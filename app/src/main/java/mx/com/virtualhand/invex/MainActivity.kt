package mx.com.virtualhand.invex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.com.virtualhand.invex.data.*
import mx.com.virtualhand.invex.domain.*
import mx.com.virtualhand.invex.ui.products.ProductsScreen
import mx.com.virtualhand.invex.ui.screens.*
import mx.com.virtualhand.invex.ui.theme.InvexTheme
import mx.com.virtualhand.invex.ui.viewmodels.*

class MainActivity : ComponentActivity() {

    private lateinit var googleLoginCallback: (GoogleSignInAccount?) -> Unit

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = try { task.result } catch (e: Exception) { null }
        googleLoginCallback(account)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Repositorios y casos de uso usando Firebase
        val authUseCase = AuthUseCase(AuthRepository(FirebaseAuth.getInstance()))
        val productRepo = ProductRepository(FirebaseFirestore.getInstance())
        val productUseCase = ProductUseCase(productRepo)
        val movementRepo = MovementRepository(FirebaseFirestore.getInstance())
        val movementUseCase = MovementUseCase(movementRepo)
        val categoryRepo = CategoryRepository(FirebaseFirestore.getInstance())
        val reportRepo = ReportRepository(FirebaseFirestore.getInstance())

        setContent {

            // ViewModels
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authUseCase)
            )

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productUseCase)
            )

            val movementsViewModel: MovementViewModel = viewModel(
                factory = MovementViewModelFactory(movementRepo)
            )

            val categoryViewModel: CategoryViewModel = viewModel(
                factory = CategoryViewModelFactory(categoryRepo)
            )

            val reportViewModel: ReportViewModel = viewModel(
                factory = ReportViewModelFactory(reportRepo)
            )

            // Estado de navegación
            val uiState by loginViewModel.uiState.collectAsState()
            var currentScreen by remember { mutableStateOf("dashboard") }

            InvexTheme {
                when {
                    // Usuario NO logueado
                    !uiState.isLoggedIn -> {
                        LoginScreen(
                            state = uiState,
                            onGoogleLogin = { startGoogleLogin(loginViewModel) }
                        )
                    }

                    // Dashboard
                    currentScreen == "dashboard" -> {
                        MainScreen(
                            loginViewModel = loginViewModel,
                            onNavigateToProducts = { currentScreen = "products" },
                            onNavigateToMovements = { currentScreen = "movements" },
                            onNavigateToReports = { currentScreen = "reports" }, // <-- agregado
                            onNavigateToCategories = { currentScreen = "categories" },
                        )
                    }

                    // Pantalla de productos
                    currentScreen == "products" -> {
                        ProductsScreen(
                            productViewModel = productViewModel,
                            onBack = { currentScreen = "dashboard" }
                        )
                    }

                    // Pantalla de movimientos
                    currentScreen == "movements" -> {
                        MovementsScreen(
                            onBack = { currentScreen = "dashboard" },
                            movementViewModel = movementsViewModel,
                            productViewModel = productViewModel
                        )
                    }

                    // Pantalla de categorías
                    currentScreen == "categories" -> {
                        CategoriesScreen(
                            categoryVM = categoryViewModel,
                            productVM = productViewModel,
                            onBack = { currentScreen = "dashboard" }
                        )
                    }

                    // Pantalla de reportes
                    currentScreen == "reports" -> {
                        ReportScreen(
                            viewModel = reportViewModel,
                            onBack = { currentScreen = "dashboard" }
                        )
                    }
                }
            }
        }
    }

    private fun startGoogleLogin(viewModel: LoginViewModel) {
        googleLoginCallback = { account ->
            if (account != null) viewModel.loginWithGoogle(account)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, gso)
        googleLauncher.launch(googleClient.signInIntent)
    }
}
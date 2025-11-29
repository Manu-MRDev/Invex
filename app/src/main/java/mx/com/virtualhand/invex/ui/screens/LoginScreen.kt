package mx.com.virtualhand.invex.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.invex.R

@Composable
fun LoginScreen(
    state: LoginUiState,
    onGoogleLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Invex Logo",
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = "Bienvenido a Invex",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (state.isLoading) "Cargando..." else "Continuar con Google",
                    color = Color.Black
                )
            }

            state.error?.let {
                Text(text = "⚠ $it", color = Color.Red)
            }
        }
    }
}

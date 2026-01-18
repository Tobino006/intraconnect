package sk.tobino.intraconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.auth.auth
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider
import sk.tobino.intraconnect.ui.navigation.AppNav
import sk.tobino.intraconnect.ui.screen.login.LoginScreen
import sk.tobino.intraconnect.ui.screen.login.LoginViewModel
import sk.tobino.intraconnect.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                val rootNavController = rememberNavController()

                var startDestination by remember { mutableStateOf<String?>(null) }

                // check if user is logged in
                LaunchedEffect(Unit) {
                    val client = SupabaseClientProvider.client
                    startDestination = if (client.auth.currentSessionOrNull() != null) {
                        "home"
                    } else {
                        "login"
                    }
                }

                if (startDestination == null) {
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavHost(navController = rootNavController, startDestination = startDestination!!) {
                        // login route
                        composable("login") {
                            val loginVm: LoginViewModel = viewModel()

                            LoginScreen (
                                viewModel = loginVm,
                                onLoginSuccess = {
                                    // after successful login, navigate to home screen
                                    // and delete login screen from backstack
                                    rootNavController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // app route
                        composable("home") {
                            AppNav (
                                onLogout = {
                                    // after logout (or change of password) switch to login and
                                    // delete app screen from backstack
                                    rootNavController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
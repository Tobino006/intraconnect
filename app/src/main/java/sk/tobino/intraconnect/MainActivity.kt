package sk.tobino.intraconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import sk.tobino.intraconnect.ui.screen.home.HomeScreen
import sk.tobino.intraconnect.ui.screen.login.LoginScreen
import sk.tobino.intraconnect.ui.screen.login.LoginViewModel
import sk.tobino.intraconnect.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                val vm: LoginViewModel = viewModel()
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    HomeScreen()
                } else {
                    LoginScreen (
                        viewModel = vm,
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}
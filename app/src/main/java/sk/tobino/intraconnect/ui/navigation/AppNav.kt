package sk.tobino.intraconnect.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sk.tobino.intraconnect.ui.screen.home.HomeScreen

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen(nav)
        }

        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            NotificationDetailScreen(nav, id)
        }
    }
}

@Composable
fun NotificationDetailScreen(nav: NavHostController, id: String) {
    Text("Detail Screen for $id")
}
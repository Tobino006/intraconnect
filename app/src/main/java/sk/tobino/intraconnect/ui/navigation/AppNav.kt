package sk.tobino.intraconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import sk.tobino.intraconnect.ui.screen.home.HomeScreen
import sk.tobino.intraconnect.ui.screen.home.HomeViewModel
import sk.tobino.intraconnect.ui.screen.home.HomeViewModelFactory
import sk.tobino.intraconnect.ui.screen.notification.NotificationDetailScreen

@Composable
fun AppNav (
    onLogout: () -> Unit
) {
    val nav = rememberNavController()

    // shared HomeViewModel for the whole NavHost
    val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory())
    val homeState = homeVm.uiState

    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen (
                nav = nav,
                onLogout = onLogout,
                vm = homeVm
            )
        }

        composable (
            route = "detail/{id}",
            arguments = listOf (
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("id") ?: return@composable

            val company = homeState.company
            NotificationDetailScreen (
                nav = nav,
                id = notificationId,
                companyLogoUrl = company?.logoUrl,
                companyColorTheme = company?.colorTheme
            )
        }
    }
}
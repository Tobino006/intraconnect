package sk.tobino.intraconnect.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import sk.tobino.intraconnect.ui.screen.settings.SettingsScreen
import sk.tobino.intraconnect.ui.screen.settings.SettingsUiState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sk.tobino.intraconnect.ui.screen.settings.SettingsViewModel
import sk.tobino.intraconnect.ui.theme.CompanyTheme
import sk.tobino.intraconnect.ui.theme.ThemeMode


@Composable
fun HomeScreen (
    nav: NavHostController,
        vm: HomeViewModel = viewModel(factory = HomeViewModelFactory())
) {
    val state = vm.uiState

    // viewModel for settings (theme mode)
    val settingsVm: SettingsViewModel = viewModel (
        factory = SettingsViewModel.provideFactory (
            application = LocalContext.current.applicationContext as android.app.Application
        )
    )
    val settingsState by settingsVm.uiState.collectAsState()

    LaunchedEffect(true) {
        vm.loadHomeData()
    }

    if (state.isLoading) {
        Box (
            Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.company == null) {
        Box (
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("No company selected")
        }
        return
    }

    var selectedIndex by remember { mutableIntStateOf(0) }

    // from settings pick whether to use CompanyTheme or not.
    if (settingsState.themeMode == ThemeMode.COMPANY) {
        CompanyTheme(state.company.colorTheme) {
            HomeScreenContent(
                nav = nav,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it },
                state = state,
                settingsState = settingsState,
                onThemeModeChange = settingsVm::setThemeMode
            )
        }
    } else {
        // APP mode
        HomeScreenContent(
            nav = nav,
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { selectedIndex = it },
            state = state,
            settingsState = settingsState,
            onThemeModeChange = settingsVm::setThemeMode
        )
    }

}

@Composable
private fun HomeScreenContent (
    nav: NavHostController,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    state: HomeUiState,
    settingsState: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeHeader (
                logoUrl = state.company?.logoUrl
            )
        },
        bottomBar = {
            HomeBottomNav (
                selectedIndex = selectedIndex,
                onItemSelected = { index -> onSelectedIndexChange(index) },
                profileAvatarUrl = state.user?.avatarUrl
            )
        }
    ) { innerPadding ->

        when (selectedIndex) {
            0 -> {
                Column(Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
                {

                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp, top = 16.dp)
                    ) {
                        items(state.notifications) { notif ->
                            NotificationCard (
                                notif = notif,
                                onClick = { nav.navigate("detail/${notif.id}") }
                            )
                        }
                    }
                }
            }

            2 -> {
                // settings tab
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    SettingsScreen (
                        state = settingsState,
                        onThemeModeChange = onThemeModeChange
                    )
                }
            }

            else -> {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text ("Screen for tab index $selectedIndex")
                }
            }
        }
    }
}
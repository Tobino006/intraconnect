package sk.tobino.intraconnect.ui.screen.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sk.tobino.intraconnect.R
import sk.tobino.intraconnect.ui.screen.home.HomeHeader
import sk.tobino.intraconnect.ui.screen.settings.SettingsViewModel
import sk.tobino.intraconnect.ui.theme.CompanyTheme
import sk.tobino.intraconnect.ui.theme.ThemeMode

@Composable
fun NotificationDetailScreen (
    nav: NavHostController,
    id: String,
    vm: NotificationDetailViewModel = viewModel(),
    companyLogoUrl: String? = null,
    companyColorTheme: String? = null
) {
    val state = vm.uiState

    // settings VM, the same as in HomeScreen
    val settingsVm: SettingsViewModel = viewModel (
        factory = SettingsViewModel.provideFactory (
            application = LocalContext.current.applicationContext as android.app.Application
        )
    )
    val settingsState by settingsVm.uiState.collectAsState()

    LaunchedEffect(id) {
        vm.loadNotification(id)
    }

    // helper composable
    @Composable
    fun Content() {
        Scaffold (
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                HomeHeader(logoUrl = companyLogoUrl)
            },
            bottomBar = {
                Box (
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button (
                        onClick = { nav.popBackStack() },
                        modifier = Modifier
                            .width(160.dp)
                            .height(60.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text (
                            text = stringResource(R.string.go_back)
                        )
                    }
                }
            }
        ) { innerPadding ->
            when {
                state.isLoading -> {
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.errorMessage)
                    }
                }

                state.notification != null -> {
                    NotificationDetailCard (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        notification = state.notification,
                        author = state.author,
                        departments = state.departments,
                        publishedText = state.publishedText,
                        updatedText = state.updatedText
                    )
                }
            }
        }
    }

    // theme
   if (settingsState.themeMode == ThemeMode.COMPANY && companyColorTheme != null) {
       CompanyTheme(companyColorTheme) {
           Content()
       }
   } else {
       // app default theme
       Content()
   }
}
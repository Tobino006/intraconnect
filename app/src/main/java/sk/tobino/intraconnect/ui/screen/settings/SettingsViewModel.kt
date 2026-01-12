package sk.tobino.intraconnect.ui.screen.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sk.tobino.intraconnect.data.repository.UserSettingsRepository
import sk.tobino.intraconnect.ui.theme.ThemeMode

data class SettingsUiState (
    val themeMode: ThemeMode = ThemeMode.APP,
    val isLoading: Boolean = true
)

class SettingsViewModel (
    application: Application,
    private val userSettingsRepository: UserSettingsRepository
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // load theme mode from datastore
        viewModelScope.launch {
            userSettingsRepository.themeModeFlow.collect { mode ->
                _uiState.update {
                    it.copy (
                        themeMode = mode,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userSettingsRepository.setThemeMode(mode)
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T: ViewModel> create(modelClass: Class<T>): T {
                    val repo = UserSettingsRepository(application)
                    return SettingsViewModel (
                        application = application,
                        userSettingsRepository = repo
                    ) as T
                }
            }
        }
    }
}
package sk.tobino.intraconnect.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sk.tobino.intraconnect.data.remote.supabase.SupabaseAuthRepository

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel (
    private val authRepository: SupabaseAuthRepository = SupabaseAuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            val result = authRepository.login (
                email = uiState.value.email,
                password = uiState.value.password
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(success = true, loading = false)
            } else {
                _uiState.value.copy(error = result.exceptionOrNull()?.message ?: "Unknown error", loading = false)
            }
        }
    }
}
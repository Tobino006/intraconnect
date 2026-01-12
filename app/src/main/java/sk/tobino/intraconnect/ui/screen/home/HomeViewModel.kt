package sk.tobino.intraconnect.ui.screen.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.tobino.intraconnect.data.model.CompanyDto
import sk.tobino.intraconnect.data.model.NotificationDto
import sk.tobino.intraconnect.domain.usecase.GetCompany
import sk.tobino.intraconnect.domain.usecase.GetNotificationsForUser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider
import sk.tobino.intraconnect.data.model.UserDto

data class HomeUiState (
    val company : CompanyDto? = null,
    val notifications : List<NotificationDto> = emptyList(),
    val isLoading : Boolean = true,
    val user: UserDto? = null
)

class HomeViewModel (
    private val getCompany : GetCompany,
    private val getNotifications : GetNotificationsForUser,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    fun loadHomeData() = viewModelScope.launch {
        setLoadingState()

        try {
            val client = SupabaseClientProvider.client

            // auth user
            val authUser = client
                .auth
                .currentSessionOrNull()
                ?.user

            if (authUser == null) {
                uiState = uiState.copy(isLoading = false, company = null, notifications = emptyList())
                return@launch
            }

            val userResult = client.postgrest["user"].select {
                filter {
                    eq("id", authUser.id)
                }
            }

            val userList : List<UserDto> = userResult.decodeList()
            val userRow = userList.firstOrNull()

            if (userRow == null) {
                Log.e("HomeViewModel", "No user row found for id=${authUser.id}")
                uiState = uiState.copy(isLoading = false, company = null, notifications = emptyList())
                return@launch
            }

            Log.d (
                "HomeViewModel",
                "Loaded user row: id=${userRow.id}, companyId=${userRow.companyId}, departmentId=${userRow.departmentId}"
            )

            val companyId = userRow.companyId
            val departmentId = userRow.departmentId

            val company = getCompany(companyId)
            Log.d("HomeViewModel", "Loaded company: ${company.name}")
            val notifications = getNotifications(companyId, departmentId)
            Log.d("HomeViewModel", "Loaded ${notifications.size} notifications")

            uiState = HomeUiState (
                company = company,
                notifications = notifications,
                isLoading = false,
                user = userRow
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error in loadHomeData", e)
            uiState = uiState.copy(isLoading = false, company = null, notifications = emptyList())
        }

    }

    private fun setLoadingState() {
        uiState = uiState.copy(isLoading = true)
    }
}
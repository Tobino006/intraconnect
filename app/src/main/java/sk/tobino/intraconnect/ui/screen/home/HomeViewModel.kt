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

enum class NotificationFilter {
    ALL,
    GLOBAL_ONLY,
    DEPARTMENT_ONLY
}

data class HomeUiState (
    val company : CompanyDto? = null,
    val notifications : List<NotificationDto> = emptyList(),
    val allNotifications: List<NotificationDto> = emptyList(),
    val isLoading : Boolean = true,
    val user: UserDto? = null,
    val notificationFilter: NotificationFilter = NotificationFilter.ALL,
    val currentOffset: Int = 0,
    val pageSize: Int = 10,
    val canLoadMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null
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
            val firstPage = getNotifications(companyId, departmentId, uiState.pageSize, 0)
            Log.d("HomeViewModel", "Loaded ${firstPage.size} notifications")
            val currentFilter = uiState.notificationFilter

            uiState = HomeUiState (
                company = company,
                notifications = applyFilter (
                    firstPage,
                    currentFilter,
                ),
                allNotifications = firstPage,
                isLoading = false,
                user = userRow,
                currentOffset = firstPage.size,
                canLoadMore = firstPage.size == uiState.pageSize,
                isLoadingMore = false,
                notificationFilter = currentFilter,
                errorMessage = null,

            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error in loadHomeData", e)
            uiState = uiState.copy(isLoading = false, company = null, notifications = emptyList(), errorMessage = e.message ?: "Error loading data")
        }

    }

    fun loadNextPage() = viewModelScope.launch {
        val companyId = uiState.user?.companyId ?: return@launch
        val departmentId = uiState.user?.departmentId

        if (!uiState.canLoadMore || uiState.isLoadingMore) return@launch

        try {
            val nextPage = getNotifications(companyId, departmentId, uiState.pageSize, uiState.currentOffset)
            val newList = uiState.notifications + nextPage
            uiState = uiState.copy (
                notifications = applyFilter(newList, uiState.notificationFilter),
                allNotifications = newList,
                currentOffset = uiState.currentOffset + nextPage.size,
                canLoadMore = newList.size == uiState.pageSize,
                isLoadingMore = false
            )
        } catch (e: Exception) {
            uiState = uiState.copy(isLoadingMore = false)
            Log.e("HomeViewModel", "Error loading next page", e)
        }
    }

    private fun setLoadingState() {
        uiState = uiState.copy(isLoading = true)
    }

    fun setNotificationFilter(filter: NotificationFilter) {
        val filtered = applyFilter (
            uiState.allNotifications,
            filter
        )
        uiState = uiState.copy (
            notificationFilter = filter,
            notifications = filtered
        )
    }

    private fun applyFilter (
        notifications: List<NotificationDto>,
        filter: NotificationFilter,
    ): List<NotificationDto> {
        return when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.GLOBAL_ONLY -> notifications.filter { it.isGlobal }
            NotificationFilter.DEPARTMENT_ONLY -> notifications.filter { !it.isGlobal }
        }
    }
}
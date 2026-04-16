package sk.tobino.intraconnect.ui.screen.notification

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import sk.tobino.intraconnect.data.model.NotificationDto
import sk.tobino.intraconnect.data.model.UserDto
import sk.tobino.intraconnect.data.remote.supabase.DepartmentRepository
import sk.tobino.intraconnect.data.remote.supabase.NotificationRepository
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider
import sk.tobino.intraconnect.ui.util.formatDate
import java.util.Locale

data class NotificationDetailUiState (
    val isLoading: Boolean = true,
    val notification: NotificationDto? = null,
    val author: UserDto? = null,
    val departments: List<String> = emptyList(),
    val publishedText: String? = null,
    val updatedText: String? = null,
    val errorMessage: String? = null
)

class NotificationDetailViewModel (
    private val notificationRepository: NotificationRepository = NotificationRepository(),
    private val departmentRepository: DepartmentRepository = DepartmentRepository()
) : ViewModel() {
    var uiState by mutableStateOf(NotificationDetailUiState())
        private set

    fun loadNotification(notificationId: String) = viewModelScope.launch {
        setLoading()

        try {
            val notification = notificationRepository.getById(notificationId)
            if (notification == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Notification not found"
                )
                return@launch
            }

            Log.d("NotificationDetailVM", "Loaded notification: $notification")
            Log.d("NotificationDetailVM", "createdBy from notification: ${notification.createdBy}")

            val client = SupabaseClientProvider.client

            // author
            val userResult = client.postgrest["user"].select {
                filter {
                    eq("id", notification.createdBy)
                }
            }
            val userList: List<UserDto> = userResult.decodeList()
            val author = userList.firstOrNull()

            // departments
            val departmentNames = if (notification.isGlobal) {
                listOf("Global")
            } else {
                val notificationDepartments =
                    notificationRepository.getDepartmentsForNotification(notification.id)
                val names = notificationDepartments.mapNotNull { nd ->
                    departmentRepository.getDepartmentName(nd.departmentId)
                }

                names.ifEmpty {
                    listOf("Unknown department")
                }
            }

            // date formatting - based on locale
            val locale = Locale.getDefault()

            val publishedText = formatDate (
                raw = notification.publishedAt,
                locale = locale
            )

            val updatedText = notification.updatedAt?.let {
                formatDate (
                    raw = it,
                    locale = locale
                )
            }


            uiState = NotificationDetailUiState(
                isLoading = false,
                notification = notification,
                author = author,
                departments = departmentNames,
                publishedText = publishedText,
                updatedText = updatedText,
                errorMessage = null
            )
        } catch (e: Exception) {
            Log.e("NotificationDetailViewModel", "Error loading notification", e)
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = e.message ?: "Error loading notification"
            )
        }
    }

    private fun setLoading() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
    }
}
package sk.tobino.intraconnect.ui.screen.profile

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.tobino.intraconnect.data.model.UserDto
import sk.tobino.intraconnect.data.remote.supabase.CompanyRepository
import sk.tobino.intraconnect.data.remote.supabase.DepartmentRepository
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider

data class ProfileUiState (
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val departmentName: String? = null,
    val companyName: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val logout: Boolean = false
)

class ProfileViewModel (
    private val departmentRepository: DepartmentRepository = DepartmentRepository(),
    private val companyRepository: CompanyRepository = CompanyRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val client get() = SupabaseClientProvider.client

    fun loadProfile() = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        try {
            val authUser = client.auth.currentSessionOrNull()?.user
                ?: run {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Not logged in")
                    return@launch
                }

            val result = client.postgrest["user"].select {
                filter {
                    eq("id", authUser.id)
                }
            }

            val userList : List<UserDto> = result.decodeList()
            val userRow = userList.firstOrNull()

            if (userRow == null) {
                uiState = uiState.copy(isLoading = false, errorMessage = "No user row found for id=${authUser.id}")
                return@launch
            }

            var departmentName: String? = null
            var companyName : String? = null

            userRow.departmentId?.let { departmentId ->
                try {
                    Log.d("ProfileViewModel", "User departmentId = $departmentId")

                    val department = departmentRepository.getDepartment(departmentId)
                    if (department != null) {
                        departmentName = department.name
                        companyName = companyRepository.getCompanyName(department.companyId)
                    } else {
                        Log.e("ProfileViewModel", "Department not found for id=$departmentId")
                    }
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Error loading department/company", e)
                }
            }

            uiState = uiState.copy(
                isLoading = false,
                user = userRow,
                name = userRow.name,
                phone = userRow.phone ?: "",
                avatarUrl = userRow.avatarUrl,
                departmentName = departmentName,
                companyName = companyName,
                errorMessage = null,
            )
        } catch (e: Exception) {
            uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "Error loading profile")
        }
    }

    fun saveProfile(phone: String, newPassword: String) = viewModelScope.launch {
        val user = uiState.user ?: return@launch

        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        try {
            // case 1: password also changes
            if (newPassword.isNotBlank()) {
                // change password
                try {
                    client.auth.updateUser {
                        password = newPassword
                    }
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Error updating password", e)

                    val message = when (e) {
                        is AuthWeakPasswordException -> "Weak password. Saving aborted."
                        else -> e.message ?: "Error updating password"
                    }
                    // failed -> nothing will save
                    uiState = uiState.copy(isLoading = false, errorMessage = message)
                    return@launch
                }

                // password is successful -> also update phone
                if (phone != uiState.phone) {
                    client.postgrest["user"].update (
                        {
                            set("phone", phone.ifBlank { null } )
                        }
                    ) {
                        filter { eq("id", user.id) }
                    }
                }

                // logout
                client.auth.signOut()

                uiState = uiState.copy(isLoading = false, logout = true)
                return@launch
            }

            // case 2: only phone changes
            client.postgrest["user"].update (
                {
                    set("phone", phone.ifBlank { null } )
                }
            ) {
                filter { eq("id", user.id) }
            }

            uiState = uiState.copy(isLoading = false, phone = phone, successMessage = "Phone number updated")
        } catch (e: Exception) {
            uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "Error updating profile")
        }
    }

    fun uploadAvatar(uri: Uri, contentResolver: ContentResolver) = viewModelScope.launch {
        val authUser = client.auth.currentSessionOrNull()?.user ?: return@launch
        uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null)

        try {
            val bytes = withContext(Dispatchers.IO) {
                contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } ?: throw IllegalStateException("Failed to read image bytes from $uri")

            val mimeTypeString = contentResolver.getType(uri) ?: "image/jpeg"
            val contentType = ContentType.parse(mimeTypeString)

            val bucket = client.storage["user_avatars"]
            bucket.upload (
                path = authUser.id,
                data = bytes,
            ) {
                upsert = true
                this.contentType = contentType
            }

            val publicUrl = bucket.publicUrl(authUser.id)

            // save url into public.user.user_avatars
            client.postgrest["user"].update (
                {
                set("avatar_url", publicUrl)
                }
            ) {
                filter { eq("id", authUser.id) }
            }

            uiState = uiState.copy(isLoading = false, avatarUrl = publicUrl, successMessage = "Avatar updated")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error uploading avatar", e)
            uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "Error uploading avatar")
        }
    }

    fun clearMessage() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }

    fun resetLogout() {
        uiState = uiState.copy(logout = false)
    }

    fun logout() {
        viewModelScope.launch {
            try {
                client.auth.signOut()
                // after successful logout, switch to login screen
                uiState = uiState.copy(logout = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "Error logging out")
            }
        }
    }
}
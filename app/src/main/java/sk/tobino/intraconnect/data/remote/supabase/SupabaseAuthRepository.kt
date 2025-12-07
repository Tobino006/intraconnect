package sk.tobino.intraconnect.data.remote.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider.client

class SupabaseAuthRepository {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun currentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }
}
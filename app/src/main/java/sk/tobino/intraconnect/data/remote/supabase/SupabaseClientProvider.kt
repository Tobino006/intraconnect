package sk.tobino.intraconnect.data.remote.supabase

import android.content.Context
import sk.tobino.intraconnect.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {
    private var _client: SupabaseClient? = null

    val client: SupabaseClient
        get() = _client ?: error("SupabaseClientProvider not initialized. Call initialize() first.")

    fun initialize(context: Context) {
        if (_client != null) return

        _client = createSupabaseClient (
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth) {
                autoLoadFromStorage = true
                autoSaveToStorage = true
            }

            install(Postgrest)
            install(Realtime)
        }
    }
}
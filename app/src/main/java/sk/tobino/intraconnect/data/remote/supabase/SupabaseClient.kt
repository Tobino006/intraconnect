package sk.tobino.intraconnect.data.remote.supabase

import sk.tobino.intraconnect.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseClientProvider {

    // Fail fast if values are missing to avoid silent misconfigurations
    private const val url: String = BuildConfig.SUPABASE_URL
    private const val key: String = BuildConfig.SUPABASE_KEY

    init {
        require(url.isNotBlank()) { "BuildConfig.SUPABASE_URL is blank. Add SUPABASE_URL to local.properties and rebuild." }
        require(key.isNotBlank()) { "BuildConfig.SUPABASE_KEY is blank. Add SUPABASE_KEY to local.properties and rebuild." }
    }

    val client = createSupabaseClient(
        supabaseUrl = url,
        supabaseKey = key
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }
}
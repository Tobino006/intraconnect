package sk.tobino.intraconnect

import android.app.Application
import sk.tobino.intraconnect.data.remote.supabase.SupabaseClientProvider

class IntraConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SupabaseClientProvider.initialize(this)
    }
}
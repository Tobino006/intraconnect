package sk.tobino.intraconnect.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sk.tobino.intraconnect.ui.theme.ThemeMode

private const val USER_SETTINGS_DATASTORE_NAME = "user_settings"

private val Context.userSettingsDataStore by preferencesDataStore(name = USER_SETTINGS_DATASTORE_NAME)

// responsible for setting/reading UI settings
class UserSettingsRepository (
    private val context: Context
) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    // flow which emits current theme mode
    val themeModeFlow: Flow<ThemeMode> =
        context.userSettingsDataStore.data.map { prefs ->
            val modeName = prefs[THEME_MODE_KEY]
            when (modeName) {
                ThemeMode.COMPANY.name -> ThemeMode.COMPANY
                ThemeMode.APP.name -> ThemeMode.APP
                else -> ThemeMode.APP // default
            }
        }

    // save theme mode
    suspend fun setThemeMode(mode: ThemeMode) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }
}
package rs.raf.exbanka.mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Wraps Android DataStore to persist the JWT access and refresh tokens.
 * Tokens are stored as plain strings (no encryption for simplicity;
 * use EncryptedSharedPreferences for production hardening).
 */
@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessToken: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_ACCESS_TOKEN] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> !prefs[KEY_ACCESS_TOKEN].isNullOrBlank() }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

    suspend fun getAccessToken(): String? {
        var token: String? = null
        context.dataStore.edit { prefs ->
            token = prefs[KEY_ACCESS_TOKEN]
        }
        return token
    }
}

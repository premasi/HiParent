package rk.enkidu.hiparent.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getThemeSettings(): Flow<Boolean> {
        return dataStore.data.map {
            it[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSettings(isDarkModeActive: Boolean){
        dataStore.edit {
            it[THEME_KEY] = isDarkModeActive
        }
    }

    companion object{
        private val THEME_KEY = booleanPreferencesKey("theme_settings")

        @Volatile
        private var instance : SettingsPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) : SettingsPreferences{
            return instance ?: synchronized(this){
                val i = SettingsPreferences(dataStore)
                instance = i
                i
            }
        }
    }

}
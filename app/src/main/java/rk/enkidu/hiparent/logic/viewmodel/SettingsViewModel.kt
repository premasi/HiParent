package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.data.preferences.SettingsPreferences

class SettingsViewModel (private val pref: SettingsPreferences) : ViewModel() {
    fun getThemeSettings() : LiveData<Boolean> {
        return pref.getThemeSettings().asLiveData()
    }

    fun saveThemeSettings(isDarkModeActive: Boolean){
        viewModelScope.launch {
            pref.saveThemeSettings(isDarkModeActive)
        }
    }

}
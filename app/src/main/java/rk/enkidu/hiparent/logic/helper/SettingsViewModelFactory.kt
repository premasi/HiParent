package rk.enkidu.hiparent.logic.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rk.enkidu.hiparent.data.preferences.SettingsPreferences
import rk.enkidu.hiparent.logic.viewmodel.SettingsViewModel
import java.lang.IllegalArgumentException

class SettingsViewModelFactory(private val preferences: SettingsPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

}
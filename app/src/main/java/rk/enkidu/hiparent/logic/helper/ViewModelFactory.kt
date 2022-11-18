package rk.enkidu.hiparent.logic.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import rk.enkidu.hiparent.data.repository.Repository
import rk.enkidu.hiparent.di.Injection
import rk.enkidu.hiparent.logic.viewmodel.LoginViewModel
import rk.enkidu.hiparent.logic.viewmodel.RegistrationViewModel

class ViewModelFactory (private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(auth: FirebaseAuth): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(auth))
            }.also { instance = it }
    }
}
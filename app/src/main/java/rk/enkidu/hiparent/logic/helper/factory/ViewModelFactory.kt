package rk.enkidu.hiparent.logic.helper.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import rk.enkidu.hiparent.data.repository.Repository
import rk.enkidu.hiparent.di.Injection
import rk.enkidu.hiparent.logic.viewmodel.*

class ViewModelFactory (private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            return ProfileViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(EditDiscussionViewModel::class.java)){
            return EditDiscussionViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(CommentsViewModel::class.java)){
            return CommentsViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            return AlarmViewModel(repository) as T
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
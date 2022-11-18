package rk.enkidu.hiparent.di

import com.google.firebase.auth.FirebaseAuth
import rk.enkidu.hiparent.data.repository.Repository

object Injection {
    fun provideRepository(auth: FirebaseAuth): Repository {
        return Repository(auth)
    }
}
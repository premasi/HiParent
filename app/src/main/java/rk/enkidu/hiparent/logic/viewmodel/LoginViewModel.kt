package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class LoginViewModel(private val repository: Repository): ViewModel() {
    fun loginUser(email: String, password: String) = repository.loginUser(email, password)
}
package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class RegistrationViewModel(private val repository: Repository) : ViewModel(){

    fun createUser(email: String, password: String) = repository.createUser(email, password)
}
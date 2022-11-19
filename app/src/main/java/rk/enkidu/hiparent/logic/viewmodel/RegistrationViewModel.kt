package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class RegistrationViewModel(private val repository: Repository) : ViewModel(){

    fun createUser(fullname: String, email: String, password: String) = repository.createUser(fullname, email, password)
}
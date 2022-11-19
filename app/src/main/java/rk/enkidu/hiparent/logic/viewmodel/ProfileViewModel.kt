package rk.enkidu.hiparent.logic.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class ProfileViewModel(private val repository: Repository): ViewModel() {

    fun updateProfile(photo: Uri, fullname: String) = repository.updateProfile(photo, fullname)
}
package rk.enkidu.hiparent.logic.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.data.entity.remote.Message
import rk.enkidu.hiparent.data.repository.Repository

class ProfileViewModel(private val repository: Repository): ViewModel() {

    private val _discuss = MutableLiveData<List<Discussion>>()
    val discuss : LiveData<List<Discussion>> =_discuss

    private val _message = MutableLiveData<List<Message>>()
    val message : LiveData<List<Message>> = _message

    fun updateNameAndProfile(id: String, name: String, photo: String) = repository.updatePostNameAndPhoto(id, name, photo)

    fun updateProfile(photo: Uri, fullname: String) = repository.updateProfile(photo, fullname)

    fun fetchDiscussion() = repository.fetchDiscussion(_discuss)

    fun fetchComments() = repository.fetchComment(_message)

    fun updateNameAndProfileComments(id: String, name: String, photo: String) = repository.updatePostNameAndPhotoComments(id, name, photo)
}
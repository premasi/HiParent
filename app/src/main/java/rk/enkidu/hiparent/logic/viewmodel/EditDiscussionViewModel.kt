package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class EditDiscussionViewModel(private val repository: Repository): ViewModel() {

    fun deletePost(id: String) = repository.deletePostDiscussion(id)

    fun updatePost(id: String, title: String, desc: String) = repository.updatePostDiscussion(id, title, desc)
}
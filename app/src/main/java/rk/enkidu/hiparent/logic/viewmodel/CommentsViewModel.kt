package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.repository.Repository

class CommentsViewModel(private val repository: Repository): ViewModel() {

    fun updateComment(id: String, text: String) = repository.updateTextComment(id, text)

    fun deleteComment(id: String) = repository.deleteComment(id)
}
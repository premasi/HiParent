package rk.enkidu.hiparent.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import rk.enkidu.hiparent.data.result.Result
import rk.enkidu.hiparent.data.utils.await

class Repository(private val auth: FirebaseAuth) {
    fun createUser(email: String, password: String) : LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try{
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val response = result.user!!
            auth.signOut()
            emit(Result.Success(response))
        } catch(e: Exception){
            e.printStackTrace()
            emit(Result.Error("Failed"))
        }
    }

    fun loginUser(email: String, password: String) : LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val response = result.user!!
            emit(Result.Success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.Error("Failed"))
        }
    }
}
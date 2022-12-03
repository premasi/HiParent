package rk.enkidu.hiparent.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.data.entity.remote.Message
import rk.enkidu.hiparent.data.result.Result
import rk.enkidu.hiparent.data.utils.await

class Repository(private val auth: FirebaseAuth) {
    fun createUser(fullname: String, email: String, password: String) : LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading)
        try{
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val response = result.user!!

            //save fullname
            UserProfileChangeRequest.Builder()
                .setDisplayName(fullname).build().also {
                    auth.currentUser?.updateProfile(it)?.await()
                }

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

    fun updateProfile(photo: Uri, fullname: String) : LiveData<Result<UserProfileChangeRequest>> = liveData {
        emit(Result.Loading)
        try {
            val result = UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .setPhotoUri(photo)
                .build().also {
                    auth.currentUser?.updateProfile(it)?.await()
                }

            emit(Result.Success(result))

        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.Error("Failed"))
        }
    }

    fun fetchDiscussion(liveData: MutableLiveData<List<Discussion>>){
        val db = Firebase.database
        val ref = db.getReference("Discussion")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val discuss : List<Discussion> = snapshot.children.map {
                    it.getValue(Discussion::class.java)!!.copy(id = it.key!!)
                }

                liveData.postValue(discuss)
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
    }

    fun updatePostNameAndPhoto(id: String, name: String, photo: String){
        val db = Firebase.database
        val ref = db.reference
        ref.child("Discussion").child(id).child("name").setValue(name)
        ref.child("Discussion").child(id).child("photoUrl").setValue(photo)
    }

    fun deletePostDiscussion(id: String){
        val discussRef = FirebaseDatabase.getInstance().getReference("Discussion").child(id)
        discussRef.removeValue()
    }

    fun updatePostDiscussion(id: String, title: String, desc: String){
        val db = Firebase.database
        val discussRef = db.reference.child("Discussion")
        discussRef.child(id).child("title").setValue(title)
        discussRef.child(id).child("desc").setValue(desc)
    }

    fun fetchComment(liveData: MutableLiveData<List<Message>>){
        val db = Firebase.database
        val ref = db.getReference("Comments")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val message : List<Message> = snapshot.children.map {
                    it.getValue(Message::class.java)!!.copy(id = it.key!!)
                }

                liveData.postValue(message)
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
    }

    fun updatePostNameAndPhotoComments(id: String, name: String, photo: String){
        val db = Firebase.database
        val ref = db.reference
        ref.child("Comments").child(id).child("name").setValue(name)
        ref.child("Comments").child(id).child("photo").setValue(photo)
    }

    fun updateTextComment(id: String, text: String){
        val db = Firebase.database
        val ref = db.reference
        ref.child("Comments").child(id).child("text").setValue(text)
    }

    fun deleteComment(id: String){
        val ref = FirebaseDatabase.getInstance().getReference("Comments").child(id)
        ref.removeValue()
    }

    fun fetchAlarm(liveData: MutableLiveData<List<Alarm>>){
        val db = Firebase.database
        val ref = db.getReference("Alarm")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data : List<Alarm> = snapshot.children.map {
                    it.getValue(Alarm::class.java)!!.copy(id = it.key!!)
                }

                liveData.postValue(data)
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
    }

    fun updateAlarm(id: String, date: String, time: String, milis: Long, title: String, desc: String){
        val db = Firebase.database
        val ref = db.reference.child("Alarm")
        ref.child(id).child("title").setValue(title)
        ref.child(id).child("desc").setValue(desc)
        ref.child(id).child("date").setValue(date)
        ref.child(id).child("time").setValue(time)
        ref.child(id).child("milis").setValue(milis)

    }

    fun deleteAlarm(id: String){
        val ref = FirebaseDatabase.getInstance().getReference("Alarm").child(id)
        ref.removeValue()
    }

}
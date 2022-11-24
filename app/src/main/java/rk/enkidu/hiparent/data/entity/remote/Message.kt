package rk.enkidu.hiparent.data.entity.remote

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message (
    val id: String? = null,
    val discussionId: String? = null,
    val text: String? = null,
    val name: String? = null,
    val photo: String? = null,
    val timestamp: Long? = null,
    val uid: String? = null
)
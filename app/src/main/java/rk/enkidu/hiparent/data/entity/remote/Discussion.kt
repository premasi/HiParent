package rk.enkidu.hiparent.data.entity.remote

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Discussion(
    val id: String? = null,
    val title: String? = null,
    val desc: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val timestamp: Long? = null,
    val uid: String? = null
) : Parcelable

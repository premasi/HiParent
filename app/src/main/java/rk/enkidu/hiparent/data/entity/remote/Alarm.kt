package rk.enkidu.hiparent.data.entity.remote

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Alarm(
    val id: String? = null,
    val date: String? = null,
    val time: String? = null,
    val milis: Long? = null,
    val title: String? = null,
    val desc: String? = null,
    val uid: String? = null
) : Parcelable

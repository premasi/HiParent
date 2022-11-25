package rk.enkidu.hiparent.data.entity.remote

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Tips (
    val id: String? = null,
    val title: String? = null,
    val desc: String? = null
) : Parcelable

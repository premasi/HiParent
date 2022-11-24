package rk.enkidu.hiparent.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rk.enkidu.hiparent.data.entity.remote.Message
import rk.enkidu.hiparent.databinding.ListCommentBinding

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private val list = ArrayList<Message>()

    fun setList(list: List<Message>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val item: ListCommentBinding): RecyclerView.ViewHolder(item.root){
        fun bind(data: Message){
            Glide.with(itemView.context)
                .load(data.photo)
                .circleCrop()
                .into(item.ivProfile)
            item.tvComment.text = data.text
            item.tvFullname.text = data.name

            if(data.timestamp != null){
                item.tvTimestamp.text = DateUtils.getRelativeTimeSpanString(data.timestamp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ListCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
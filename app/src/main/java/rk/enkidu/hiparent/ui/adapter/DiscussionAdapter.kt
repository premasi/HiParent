package rk.enkidu.hiparent.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.DiscussionListBinding

class DiscussionAdapter(option: FirebaseRecyclerOptions<Discussion>): FirebaseRecyclerAdapter<Discussion, DiscussionAdapter.DiscussionViewHolder>(option) {
    inner class DiscussionViewHolder(private val item: DiscussionListBinding): RecyclerView.ViewHolder(item.root) {
        fun bind(data: Discussion){
            if(data.id != null){
                item.tvTitleList.text = data.title
                item.tvFullnameList.text = data.name

                if(data.timestamp != null){
                    item.tvDate.text = DateUtils.getRelativeTimeSpanString(data.timestamp)
                }
            } else {
                Toast.makeText(itemView.context, itemView.context.getString(R.string.result_empty), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.discussion_list, parent, false)
        val binding = DiscussionListBinding.bind(view)
        return DiscussionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int, model: Discussion) {
        holder.bind(model)
    }

}
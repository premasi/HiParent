package rk.enkidu.hiparent.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.DiscussionListBinding

class DiscussionPrivateAdapter : RecyclerView.Adapter<DiscussionPrivateAdapter.PrivateViewHolder>() {
    private val list = ArrayList<Discussion>()

    fun setList(list: List<Discussion>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class PrivateViewHolder(private val item: DiscussionListBinding): RecyclerView.ViewHolder(item.root){
        fun bind(data: Discussion){
            item.tvTitleList.text = data.title
            item.tvFullnameList.text = data.name

            if(data.timestamp != null){
                item.tvDate.text = DateUtils.getRelativeTimeSpanString(data.timestamp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateViewHolder {
        val item = DiscussionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrivateViewHolder(item)
    }

    override fun onBindViewHolder(holder: PrivateViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
package rk.enkidu.hiparent.ui.adapter

import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.DiscussionListBinding
import rk.enkidu.hiparent.ui.forum.detail.DetailActivity

class DiscussionAdapter : RecyclerView.Adapter<DiscussionAdapter.PrivateViewHolder>() {
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

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DATA_DETAIL, data)
                itemView.context.startActivity(intent)
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
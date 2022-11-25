package rk.enkidu.hiparent.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rk.enkidu.hiparent.data.entity.remote.Tips
import rk.enkidu.hiparent.databinding.ListTipsBinding
import rk.enkidu.hiparent.ui.tips.detail.DetailTipsActivity

class TipsAdapter : RecyclerView.Adapter<TipsAdapter.ViewHolder>() {
    private val list = ArrayList<Tips>()

    fun setList(list: List<Tips>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val item: ListTipsBinding): RecyclerView.ViewHolder(item.root){
        fun bind(data: Tips){
            item.tvTitleList.text = data.title
            item.tvBy.text = data.desc?.substring(0, 20) + "..."

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailTipsActivity::class.java)
                intent.putExtra(DetailTipsActivity.DATA_DETAIL, data)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ListTipsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
package rk.enkidu.hiparent.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.databinding.ListAlarmBinding
import rk.enkidu.hiparent.ui.home.edit.EditAlarmActivity

class AlarmAdapter: RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    private val list = ArrayList<Alarm>()

    fun setList(list: List<Alarm>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val item: ListAlarmBinding): RecyclerView.ViewHolder(item.root){
        fun bind(data: Alarm){
            item.tvTitleList.text = data.title
//            item.tvDate.text = data.date
            item.tvTime.text = data.time

            itemView.setOnClickListener {
                val intent = Intent(it.context, EditAlarmActivity::class.java)
                intent.putExtra(EditAlarmActivity.DATA, data)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ListAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
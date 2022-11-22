package rk.enkidu.hiparent.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rk.enkidu.hiparent.data.entity.local.Banner
import rk.enkidu.hiparent.databinding.BannerContainerBinding

class HomeBannerAdapter(private val imageList : List<Banner>) : RecyclerView.Adapter<HomeBannerAdapter.BannerViewHolder>(){
    class BannerViewHolder(private val itemBinding: BannerContainerBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(data: Banner){
            Glide.with(itemView)
                .load(data.imageUri)
                .into(itemBinding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemBinding = BannerContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

}
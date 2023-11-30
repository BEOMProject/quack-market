package com.example.quack_market.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quack_market.dao.PostModel
import com.example.quack_market.databinding.MypageItemBinding
import java.text.DecimalFormat

class MypageAdapter(private val postList: MutableList<PostModel>)
    : RecyclerView.Adapter<MypageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MypageItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    private lateinit var itemClickListener : OnItemClickListener
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = postList[position]

        holder.title.text = list.title

        val decimal = DecimalFormat("#,###")
        holder.price.text = decimal.format(list.price).toString()

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.bind(postList[position])
    }
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(private val binding: MypageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.textTitle
        val price = binding.textPrice

        fun bind(postModel: PostModel) {
            if (postModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.imageProduct)
                    .load(postModel.imageUrl)
                    .into(binding.imageProduct)
            }
        }
    }

}
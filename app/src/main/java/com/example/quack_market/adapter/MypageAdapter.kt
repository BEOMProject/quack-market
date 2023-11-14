package com.example.quack_market.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quack_market.databinding.MypageItemBinding

data class Product(val price: Int, val title: String)

class ViewHolder(val binding: MypageItemBinding): RecyclerView.ViewHolder(binding.root)

class MypageAdapter(val context: Context, val products: MutableList<Product>) : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MypageItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lists = products[position]

        holder.binding.textPrice.text = lists.price.toString()
        holder.binding.textTitle.text = lists.title
    }

    override fun getItemCount(): Int {
        return products.size
    }


}
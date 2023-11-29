package com.example.quack_market.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quack_market.dao.PostModel
import com.example.quack_market.databinding.PostItemBinding
import com.example.quack_market.navigation.BoardFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class BoardPostAdapter(private val context: BoardFragment, private val itemClickListener: OnPostItemClickListener) :
    ListAdapter<PostModel, BoardPostAdapter.ViewHolder>(diffUtil) {

    interface OnPostItemClickListener {
        fun onPostItemClick(postModel: PostModel)
    }

    inner class ViewHolder(private val binding: PostItemBinding): RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(postModel: PostModel) {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(postModel.createdAt)
            val formatDate = date?.let { SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(it) }
            binding.boardDateTextView.text = formatDate

            binding.boardTitleTextView.text = postModel.title

            if (postModel.onSale) {
                val decimal = DecimalFormat("#,###")
                binding.boardPriceTextView.text = decimal.format(postModel.price).toString()+"원"
            }
            else {
                binding.boardPriceTextView.text = "판매 완료"
            }

            if (postModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.boardPostImageView)
                    .load(postModel.imageUrl)
                    .into(binding.boardPostImageView)
            }

            binding.root.setOnClickListener {
                itemClickListener.onPostItemClick(postModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostModel>() {
            override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
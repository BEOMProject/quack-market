package com.example.quack_market.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quack_market.databinding.PostItemBinding
import com.example.quack_market.navigation.PostModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoardPostAdapter() : ListAdapter<PostModel, BoardPostAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: PostItemBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(postModel: PostModel) {
            try {
                val date = Date()
                val formattedDate = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(date)

                binding.boardDateTextView.text = formattedDate
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                binding.boardDateTextView.text = "날짜 형식 오류"
            }

            // 나머지 코드는 그대로 유지
            binding.boardTitleTextView.text = postModel.title
            binding.boardPriceTextView.text = postModel.price.toString() + " 원"

            if (postModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.boardPostImageView)
                    .load(postModel.imageUrl)
                    .into(binding.boardPostImageView)
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
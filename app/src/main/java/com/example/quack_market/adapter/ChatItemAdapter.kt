package com.example.quack_market.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quack_market.data.ChatItem
import com.example.quack_market.databinding.ItemChatBinding
import com.example.quack_market.databinding.ItemOtherchatlistBinding

class ChatItemAdapter(private val uid: String, function: (ChatItem) -> Unit) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(diffUtil) {

    inner class MyMessageViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatItem: ChatItem){
            binding.messageTextView.text = chatItem.content
            binding.dateTextView.text = chatItem.time

        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemOtherchatlistBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatItem: ChatItem){
            binding.messageTextView.text = chatItem.content
            binding.dateTextView.text = chatItem.time
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatItem = getItem(position)
        return if(chatItem.senderUid == uid) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 0) {
            MyMessageViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            OtherMessageViewHolder(ItemOtherchatlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MyMessageViewHolder -> holder.bind(getItem(position))
            is OtherMessageViewHolder -> holder.bind(getItem(position))
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

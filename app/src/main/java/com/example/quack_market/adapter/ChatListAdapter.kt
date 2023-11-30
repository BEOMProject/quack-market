package com.example.quack_market.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quack_market.data.ChatItem
import com.example.quack_market.data.ChatRoomItem
import com.example.quack_market.databinding.ItemChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListAdapter(val onItemClicked: (ChatRoomItem) -> Unit) : ListAdapter<ChatRoomItem, ChatListAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemChatlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val chatRoomItem = getItem(position)
                    onItemClicked.invoke(chatRoomItem)
                }
            }
        }
        fun bind(chatListItem: ChatRoomItem) {
            binding.root.setOnClickListener{
                onItemClicked(chatListItem)
            }

            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUserUid == chatListItem.user1Uid) {
                binding.ChatNameTextView.text = chatListItem.user2Name
            } else {
                binding.ChatNameTextView.text = chatListItem.user1Name
            }
            val lastMessageDB = Firebase.database.getReference("chatRoom")
            lastMessageDB.child(chatListItem.chatRoomId).child("lastMessage").get().addOnSuccessListener {
                binding.ChatTextView.text = it.value.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatlistBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoomItem>() {

            override fun areItemsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem.lastMessageTime == newItem.lastMessageTime
            }

            override fun areContentsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}


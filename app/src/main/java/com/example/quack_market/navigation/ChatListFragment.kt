package com.example.quack_market.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quack_market.ChatRoomActivity
import com.example.quack_market.adapter.ChatListAdapter
import com.example.quack_market.data.ChatRoomItem
import com.example.quack_market.databinding.FragmentChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatlistBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserUid = auth.currentUser?.uid
    private lateinit var chatListDB: DatabaseReference
    private var chatRoomList = mutableListOf<ChatRoomItem>()
    private lateinit var adapter: ChatListAdapter
    private var imageUrl: String? = null
    private var title: String? = null
    private var price: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatListAdapter { chatRoomItem ->
            navigateToChatRoom(chatRoomItem)
        }

        binding.chatListRecyclerView.adapter = adapter
        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        getChatRooms()
    }

    private fun navigateToChatRoom(chatRoomItem: ChatRoomItem) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomItem.chatRoomId)
        val sellerUid: String = if (chatRoomItem.user1Uid.toString() == currentUserUid) {
            chatRoomItem.user2Uid.toString()
        } else {
            chatRoomItem.user1Uid.toString()
        }
        intent.putExtra("sellerUid", sellerUid)
        intent.putExtra("image",imageUrl)
        intent.putExtra("title",title)
        intent.putExtra("price",price)

        startActivity(intent)
    }



    private fun getChatRooms() {
        val currentUserUid = auth.currentUser?.uid
        chatListDB = FirebaseDatabase.getInstance().reference.child("chatRoom")

        chatListDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList.clear()

                for (chatSnapshot in snapshot.children) {
                    val model = chatSnapshot.getValue(ChatRoomItem::class.java)
                    model?.let {
                        if (currentUserUid == it.user1Uid || currentUserUid == it.user2Uid) {
                            chatRoomList.add(it)
                        }
                    }
                }

                adapter.submitList(chatRoomList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

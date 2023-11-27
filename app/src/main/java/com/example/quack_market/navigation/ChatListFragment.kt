package com.example.quack_market.navigation

import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatlistBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var chatListDB: DatabaseReference
    private var chatRoomList = mutableListOf<ChatRoomItem>()
    private lateinit var adapter: ChatListAdapter

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
            navigateToChatRoom(chatRoomItem.chatRoomId)
        }

        binding.chatListRecyclerView.adapter = adapter
        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        getChatRooms()
    }

    private fun navigateToChatRoom(chatRoomId: String) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomId)
        startActivity(intent)
        Toast.makeText(requireContext(), "Clicked on chat room: $chatRoomId", Toast.LENGTH_SHORT).show()
    }


    private fun getChatRooms() {
        val currentUserUid = auth.currentUser?.uid
        val userChatListDB = FirebaseDatabase.getInstance().reference.child("chatRoom").child(currentUserUid ?: "")

        userChatListDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatRoomItem = snapshot.getValue(ChatRoomItem::class.java)
                chatRoomItem?.let {
                    if (!chatRoomList.contains(it)) {
                        chatRoomList.add(it)
                        adapter.submitList(chatRoomList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}

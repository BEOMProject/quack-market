package com.example.quack_market

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quack_market.adapter.ChatItemAdapter
import com.example.quack_market.data.ChatItem
import com.example.quack_market.data.ChatRoomItem
import com.example.quack_market.data.DBKey
import com.example.quack_market.databinding.ActivityChatroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatroomBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var chatDB: DatabaseReference
    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter { _: ChatItem -> }
    private var sellerUid: String? = null
    private var sellerName: String? = null
    private var buyerName: String? = null
    private lateinit var chatListDB: DatabaseReference
    private val currentUserUid = auth.currentUser?.uid
    private lateinit var chatRoomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sellerUid = intent.getStringExtra("sellerUid")
        chatRoomId = generateChatRoomId(currentUserUid, sellerUid)
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatListDB = FirebaseDatabase.getInstance().reference.child("chatRoom")

        // Get the sellerUid from the Intent
        sellerUid = intent.getStringExtra("sellerUid")

        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        binding.quit.setOnClickListener {
            finish()
        }
        val nicknameDB = Firebase.database.getReference("users")
        nicknameDB.child("$sellerUid").child("name").get().addOnSuccessListener {
            sellerName = it.value.toString()
            binding.senderTextView.text = sellerName

        }
        nicknameDB.child("$currentUserUid").child("name").get().addOnSuccessListener {
            buyerName = it.value.toString()
        }
        setupChatDatabase()
    }


    private fun setupChatDatabase() {

        chatDB = FirebaseDatabase.getInstance().reference.child(DBKey.CHILD_CHAT).child(chatRoomId)

        chatDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem?.let {
                    chatList.add(it)
                    adapter.submitList(chatList.toList())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale("ko", "KR"))
            .format(Date(System.currentTimeMillis()))

        var chatRoomItem: ChatRoomItem? = null

        if (sellerUid != null && currentUserUid != null) {
            val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale("ko", "KR"))
                .format(Date(System.currentTimeMillis()))

            chatRoomItem = ChatRoomItem(
                user1Uid = currentUserUid,
                user2Uid = sellerUid!!,
                user2Name = sellerName ?: "",
                user1Name = buyerName ?: "",
                chatRoomId = chatRoomId,
                lastMessageTime = nowTime
            )
        }

        chatRoomItem?.let {
            chatListDB.child(chatRoomId).setValue(chatRoomItem)

        }
    }


        private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val currentUserUid = auth.currentUser?.uid ?: return
            val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))
                .format(Date(System.currentTimeMillis()))

            val chatItem = ChatItem(
                senderUid = currentUserUid,
                content = messageText,
                time = nowTime,
                receiverUid = sellerUid.toString(),
                chatRoomId = chatRoomId
            )

            chatDB.push().setValue(chatItem)
            binding.messageEditText.text.clear()
        }
    }
    private fun generateChatRoomId(uid1: String?, uid2: String?): String {
        val sortedUids = listOfNotNull(uid1, uid2).sorted()
        return if (sortedUids.size >= 2) {
            "${sortedUids[0]}_${sortedUids[1]}"
        } else {""
        }
    }


}
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
    private val adapter = ChatItemAdapter { _: ChatItem -> /* Handle item click */ }
    private var sellerUid: String? = null
    //private var postId: String? = null
    private lateinit var postId: String
    private lateinit var sellerName: String
    private lateinit var chatListDB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatListDB = FirebaseDatabase.getInstance().reference.child("chatRoom")
            .child(auth.currentUser?.uid ?: "")


        sellerUid = intent.getStringExtra("sellerUid")
        //postId = intent.getStringExtra("postId") ?: ""

        /*val postIdReference = Firebase.database.getReference("post").child(postId)
        postIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postId = snapshot.key.toString()
                setupChatDatabase()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

         */

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

            setupChatDatabase()

        }
    }

    private fun setupChatDatabase() {
        val currentUserUid = auth.currentUser?.uid ?: return

        val chatRoomId = generateChatRoomId(currentUserUid, sellerUid)
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

        val chatRoomItem = ChatRoomItem(
            chatRoomId = chatRoomId,
            lastMessageTime = nowTime,
            sellerName = sellerName
        )

        chatListDB.child(chatRoomItem.chatRoomId).setValue(chatRoomItem)
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
                receiverUid = sellerUid.toString()
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
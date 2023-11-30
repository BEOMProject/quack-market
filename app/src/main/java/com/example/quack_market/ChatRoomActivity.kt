package com.example.quack_market

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quack_market.adapter.ChatItemAdapter
import com.example.quack_market.data.ChatItem
import com.example.quack_market.data.ChatRoomItem
import com.example.quack_market.data.DBKey
import com.example.quack_market.databinding.ActivityChatroomBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatroomBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var chatDB: DatabaseReference
    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter(auth.currentUser?.uid ?: "") { _: ChatItem -> }
    private var sellerUid: String? = null
    private var sellerName: String? = null
    private var buyerName: String? = null
    private lateinit var chatListDB: DatabaseReference
    private val currentUserUid = auth.currentUser?.uid
    private lateinit var chatRoomId: String
    private var imageUrl: String? = null
    private var title: String? = null
    private var price: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        })

        sellerUid = intent.getStringExtra("sellerUid")
        chatRoomId = generateChatRoomId(currentUserUid, sellerUid)
        imageUrl = intent.getStringExtra("image")
        title = intent.getStringExtra("title")
        price = intent.getLongExtra("price", 0L)



        val decimal = DecimalFormat("#,###")
        chatListDB = Firebase.database.getReference("chatRoom")

        if (imageUrl != null && title != null && price != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.ivThumbnail)
            binding.titleTextView.text = title
            binding.priceTextView.text = "${decimal.format(price!!)}원"

        } else {
            val lastDB = Firebase.database.getReference("chatRoom")
            lastDB.child(chatRoomId).child("lastImg").get().addOnSuccessListener {
                val imageUrl = it.value as String?
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.ivThumbnail)
            }

            lastDB.child(chatRoomId).child("lastTitle").get().addOnSuccessListener {
                binding.titleTextView.text = it.value.toString()
            }
            lastDB.child(chatRoomId).child("lastPrice").get().addOnSuccessListener {
                val price = it.value as? Long
                if (price != null) {
                    binding.priceTextView.text = "${decimal.format(price)}원"
                } else {
                    binding.priceTextView.text = "가격 정보 없음"
                }
            }
        }
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

        val sellerNameTask = nicknameDB.child("$sellerUid").child("name").get()
        val buyerNameTask = nicknameDB.child("$currentUserUid").child("name").get()

        Tasks.whenAllSuccess<Any>(sellerNameTask, buyerNameTask).addOnSuccessListener {
            sellerName = sellerNameTask.result?.value.toString()
            buyerName = buyerNameTask.result?.value.toString()

            setupChatDatabase()
        }




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

        chatListDB.child(chatRoomId).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                if (sellerUid != null && currentUserUid != null) {
                    val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale("ko", "KR")).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }.format(Date(System.currentTimeMillis()))

                    val chatRoomItem = ChatRoomItem(
                        user1Uid = currentUserUid,
                        user2Uid = sellerUid!!,
                        user2Name = sellerName ?: "",
                        user1Name = buyerName ?: "",
                        chatRoomId = chatRoomId,
                        lastMessageTime = nowTime
                    )

                    chatListDB.child(chatRoomId).setValue(chatRoomItem)
                }
            }
        }
    }



        private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val currentUserUid = auth.currentUser?.uid ?: return
            val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale("ko", "KR")).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(Date(System.currentTimeMillis()))

            val chatItem = ChatItem(
                senderUid = currentUserUid,
                content = messageText,
                time = nowTime,
                receiverUid = sellerUid.toString(),
                chatRoomId = chatRoomId
            )

            chatDB.push().setValue(chatItem)

            chatListDB.child(chatRoomId).child("lastMessage").setValue(messageText)
            chatListDB.child(chatRoomId).child("lastImg").setValue(imageUrl)
            chatListDB.child(chatRoomId).child("lastTitle").setValue(title)
            chatListDB.child(chatRoomId).child("lastPrice").setValue(price)

            binding.messageEditText.text.clear()
        }
    }
    private fun generateChatRoomId(uid1: String?, uid2: String?): String {
        return if (uid1 != null && uid2 != null) {
            if (uid1 < uid2) "${uid1}_$uid2" else "${uid2}_$uid1"
        } else ""
    }

}
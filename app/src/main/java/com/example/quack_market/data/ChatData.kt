package com.example.quack_market.data

import android.os.Message
import java.io.Serializable


data class ChatItem (
    val time: String,
    val senderUid: String,
    val content: String,
    val receiverUid: String,
    val chatRoomId: String
){
    constructor():this("","","","","")
}
data class ChatRoomItem(
    val user1Uid: Any ,
    val user2Uid: Any,
    val user1Name: String ,
    val user2Name: String ,
    val chatRoomId: String,
    var lastMessageTime: String
){
    constructor():this("","","","","","")
}


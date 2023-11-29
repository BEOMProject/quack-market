package com.example.quack_market.data

import android.os.Message
import java.io.Serializable


data class ChatItem (
    val time: String,
    val senderUid: String,
    val content: String,
    val receiverUid: String
){
    constructor():this("","","","")
}
data class ChatRoomItem(
    val sellerName: String = "",
    val lastMessageTime: String = "",
    val chatRoomId: String = "",
    var sellerUid: Any,
    var buyerUid: Any
){
    constructor():this("","","","","")
}


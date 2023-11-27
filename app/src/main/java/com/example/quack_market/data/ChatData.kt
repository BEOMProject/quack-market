package com.example.quack_market.data


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
    val chatRoomId: String = ""
){
    constructor():this("","","")
}


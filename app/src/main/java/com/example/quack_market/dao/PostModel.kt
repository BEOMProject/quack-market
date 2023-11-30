package com.example.quack_market.dao

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class PostModel(
    var postId: String,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val createdAt: String,
    val description: String,
    val sellerId: String,
    val onSale: Boolean
) : Parcelable {
    constructor() : this("","", "", 0, "", "", "",true)

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(imageUrl)
        dest.writeLong(price)
        dest.writeString(createdAt)
        dest.writeString(description)
        dest.writeString(sellerId)
        dest.writeByte(if (onSale) 1 else 0)
    }

}
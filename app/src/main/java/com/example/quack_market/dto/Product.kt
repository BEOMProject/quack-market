package com.example.quack_market.dto

data class Product(
    val price: Int,
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String,
    val onSale: Boolean,
    val postId: String
)
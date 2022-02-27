package com.auction

data class Bid(
    val madeAt: Int = 0,
    val bidderId: String = "",
    val itemId: String = "",
    val amount: Float = 0.00f
)

package com.auction

class Auction(
    val id: String = "",
    val listedAt: Int = 0,
    val sellerId: String = "",
    val reservePrice: Float = 0.00f,
    val closeTime: Int = 0,
    var status: String = "IN PROGRESS",
    var bids: MutableList<Bid> = mutableListOf(),
    private var highestBid: Bid = Bid(),
    private var lowestBid: Bid = Bid(),
    var totalBids: Int = 0,
    var closingPrice: Float = 0.00f,
    var buyerId: String = ""
) {
    val UNSOLD_STATUS = "UNSOLD"
    val SOLD_STATUS = "SOLD"

    fun addBid(bid: Bid): String {
        var error = validateBid(bid)

        if (!error.isNullOrEmpty()) {
            return error	
        }
		
        bids.add(bid)
        this.totalBids += 1
        return ""
    }

    fun end(): Auction {
        var highestBid = getHighestBid()
        var status = calculateStatus(highestBid)

        this.status = status
        this.highestBid = highestBid
        this.lowestBid = getLowestBid()
        this.closingPrice = caclulateClosingPrice(status)
        this.buyerId = if (status == "SOLD") highestBid.bidderId else ""

        return this
    }

    fun highestBidAmount(): Float {
        return this.highestBid.amount
    }

    fun lowestBidAmount(): Float {
        return this.lowestBid.amount
    }

    fun calculateStatus(highestBid: Bid): String {
        if (highestBid.amount > this.reservePrice) {
            return SOLD_STATUS
        } else {
            return UNSOLD_STATUS
        }
    }

    fun caclulateClosingPrice(status: String): Float {
        if (status == SOLD_STATUS) {
            if (bids.size == 1) {
                return this.reservePrice
            }

            var secondHighestBid = bids[bids.size - 1]
            if (secondHighestBid.amount >= this.reservePrice) {
                return secondHighestBid.amount
            } else {
                return bids.last().amount
            }
        }

        return 0.00f
    }

    fun getHighestBid(): Bid {
        if (bids.size > 0) {
            return bids.last()
        } else {
            return Bid()
        }
    }

    fun getLowestBid(): Bid {
        if (bids.size > 0) {
            return bids.first()
        } else {
            return Bid()
        }
    }

    fun validateBid(bid: Bid): String {
        val currentMaxBidAmount = maxBidAmount()

        if (bid.amount <= currentMaxBidAmount) {
            return String.format("Bid of %s needs to be larger than existing max bid of %s", bid.amount, currentMaxBidAmount)
        }

        if (bid.madeAt > closeTime) {
            return String.format("This auction closed at: %s. The time now is: %s", closeTime, bid.madeAt)
        }

        return ""
    }

    fun maxBidAmount(): Float {
        var currentMaxBidAmount = 0.00f

        bids.forEach {
            if (currentMaxBidAmount < it.amount) {
                currentMaxBidAmount = it.amount
            }
        }

        return currentMaxBidAmount
    }
}

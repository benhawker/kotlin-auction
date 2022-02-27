package com.auction

class Orchestrator() {
    val auctionRegistry = mutableMapOf<String, Auction>()
    val expiryRegistry = mutableMapOf<Int, MutableList<String>>()

    fun registerListing(rowData: List<String>): Pair<Auction, String> {
        val auction = Auction(
            id = rowData[3],
            listedAt = rowData[0].toInt(),
            sellerId = rowData[1],
            reservePrice = rowData[4].toFloat(),
            closeTime = rowData[5].toInt()
        )

        // Check if the item already exists in the registry
        if (auctionRegistry.containsKey(auction.id) == true) {
            var errorMsg = String.format("An auction with id: %s already exists", auction.id)
            return Pair(auction, errorMsg)
        }

        // If not add it
        auctionRegistry.put(auction.id, auction)
   		
        // Then Append to expiryRegistry
        val current = expiryRegistry.get(auction.closeTime)
        val updated = if (current != null) {
            current.plus(auction.id).toMutableList()
        } else {
            mutableListOf(auction.id)
        }

        expiryRegistry.put(auction.closeTime, updated)

        return Pair(auction, "")
    }

    fun registerBid(rowData: List<String>): Pair<Bid, String> {
        val bid = Bid(
            madeAt = rowData[0].toInt(),
            bidderId = rowData[1],
            itemId = rowData[3],
            amount = rowData[4].toFloat()
        )

        val auction = auctionRegistry.get(bid.itemId)

        if (auction == null) {
            return Pair(bid, "Auction does not (and never has) existed")
        }

        if (auction.status != "IN PROGRESS") {
            return Pair(bid, "This auction has ended")
        }

        val err = auction.addBid(bid)
        return bid to err
    }

    fun handleEndingAuctions(timestamp: Int): MutableList<String?> {
        val output = mutableListOf<String?>()

        if (expiryRegistry.containsKey(timestamp) == true) {
            val endingAuctionsList = expiryRegistry.get(timestamp)

            endingAuctionsList?.forEach {
                var auction = auctionRegistry.get(it)
                auction!!.end()

                val o = String.format(
                    "%d|%s|%s|%s|%.2f|%d|%.2f|%.2f",
                    auction.closeTime,
                    auction.id,
                    auction.buyerId,
                    auction.status,
                    auction.closingPrice,
                    auction.totalBids,
                    auction.lowestBidAmount(),
                    auction.highestBidAmount(),
                )

                output.add(o)
            }
        }

        return output
    }
}

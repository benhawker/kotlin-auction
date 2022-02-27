
package com.auction
import java.io.File

fun main() {
    val orchestrator = Orchestrator()
    File("inputs/basic.txt").forEachLine { handleRow(it.split("|"), orchestrator) }
}

fun handleRow(rowData: List<String>, orchestrator: Orchestrator) {
    if (rowData.size == 1) {
        // no-op (heartbeat)
    } else if (rowData[2] == "SELL") {
        val (auction, error) = orchestrator.registerListing(rowData)
        logError(auction, error)
    } else if (rowData[2] == "BID") {
        val (bid, error) = orchestrator.registerBid(rowData)
        logError(bid, error)
    }

    val timestamp = rowData[0].toInt()
    var output = orchestrator.handleEndingAuctions(timestamp)

    if (output.size > 0) {
        println("CLOSE TIME|ITEM ID|BUYER ID|STATUS|CLOSING PRICE|TOTAL BIDS|LOWEST BID|HIGHEST BID")
        output.forEach {
            println(it)
        }
    }
}

fun logError(obj: Any, error: String) {
    if (error.isNullOrEmpty()) {
        return
    }

    var formattedError = when (obj) {
        is Auction -> String.format("%s for auction attempting to be listed at %s", error, obj.listedAt)
        is Bid -> String.format("%s for %s", error, obj.toString())
        else -> ""
    }

    println(formattedError)
}

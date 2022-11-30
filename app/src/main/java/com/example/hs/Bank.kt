package com.example.hs

import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class Bank {

    private val receivers = arrayOf(
        "FreshBurgers", "PCI Express Delivery", "GameStore", "Hyperskill", "NearbyGroceries", "HyperCell Telecom",
        "Hot Coffee", "HyperNet ISP", "Kalinin Fried Chicken", "Code Review Industries, Ltd", "CPUShop", "Pear, Inc",
        "FOSS Donations", "RC Aviation Store", "GPUStore",
    )
    private val accounts = arrayOf("Debit card", "Credit card")
    private val statuses = Item.Transaction.Status.values()
    private val idGen = AtomicInteger()

    fun generateTransactions(from: Int = 1, until: Int = 7): List<Item.Transaction> =
        List(Random.nextInt(from, until)) { generateTransaction() }.asReversed()

    private var lastTransactionTime = Instant.now().minusSeconds(365 * 86400)
    fun generateTransaction() =
        Item.Transaction(
            idGen.getAndIncrement(),
            getRandomReceiver(),
            getRandomAccount(),
            getRandomTransactionAmount(),
            getRandomStatus(),
            Instant.ofEpochSecond(Random.nextLong(lastTransactionTime.epochSecond, Instant.now().epochSecond))
                .also { lastTransactionTime = it }
        )

    private fun getRandomReceiver(): String =
        receivers.random()

    private fun getRandomAccount(): String =
        accounts.random()

    private fun getRandomTransactionAmount(): Long =
        Random.nextInt(1_00, 512_00).toLong()

    private fun getRandomStatus(): Item.Transaction.Status =
        statuses.random()

    fun update(transactions: List<Item.Transaction>) =
        generateTransactions(0, 3) +
            transactions.map {
                if (it.status == Item.Transaction.Status.PROCESSING) it.copy(
                    receiver = when (Random.nextInt(3)) {
                        0 -> it.receiver
                        1 -> it.receiver + "'s partner"
                        2 -> it.receiver + "'s agent"
                        else -> throw AssertionError()
                    },
                    amount = if (Random.nextBoolean()) it.amount * 9 / 10 else it.amount,
                    status = statuses.random(),
                )
                else it
            }

}

package com.example.hs

import kotlin.random.Random

class Bank {

    private val receivers = listOf("FreshBurgers", "NewPost Delivery", "GameStore", "Hyperskill", "NearbyGroceries", "MyCellularProvider", "Coffee Home")
    private val accounts = listOf("Debit card", "Credit card")
    private val status = listOf<String>("Successful", "Failed")
    private val random = Random
    fun generateTransactions(): ArrayList<Transaction>{
        var list = ArrayList<Transaction>()
        for (i in 1..random.nextInt(7) + 1) {
            list.add(
                Transaction(getRandomReceiver(), getRandomAccount(), getRandomTransactionAmount(), getRandomStatus())
            )
        }
        return list
    }

    private fun getRandomReceiver(): String {
        return receivers.random()
    }

    private fun getRandomAccount(): String {
        return accounts.random()
    }

    private fun getRandomTransactionAmount(): String {
        return "$${random.nextInt(1, 100)}.00"
    }

    private fun getRandomStatus(): String {
        return status.random()
    }

}
package com.example.hs

import java.time.Instant
import java.time.LocalDate

sealed class Item {

    data class Transaction(
        val id: Int,
        val receiver: String,
        val account: String,
        val amount: Long,
        val status: Status,
        val time: Instant,
    ) : Item() {
        enum class Status {
            PROCESSING, SUCCESSFUL, FAILED,
        }
    }

    class Day(
        val day: LocalDate,
    ) : Item() {
        override fun equals(other: Any?): Boolean =
            other is Day && day == other.day
    }
}
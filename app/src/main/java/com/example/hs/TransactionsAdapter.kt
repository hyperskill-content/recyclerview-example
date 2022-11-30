package com.example.hs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.EnumSet

enum class ChangeField {
    RECEIVER, AMOUNT, STATUS,
}

class TransactionsAdapter(transactions: List<Item>) :
    ListAdapter<Item, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                when (oldItem) {
                    is Item.Day -> oldItem == newItem
                    is Item.Transaction -> newItem is Item.Transaction &&
                        oldItem.id == newItem.id
                }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem

            override fun getChangePayload(oldItem: Item, newItem: Item): Any? =
                if (oldItem is Item.Transaction && newItem is Item.Transaction) listOfNotNull(
                    ChangeField.RECEIVER.takeIf { oldItem.receiver != newItem.receiver },
                    ChangeField.AMOUNT.takeIf { oldItem.amount != newItem.amount },
                    ChangeField.STATUS.takeIf { oldItem.status != newItem.status },
                ) else null
        }) {

    init {
        submitList(transactions)
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Item.Day -> 0
            is Item.Transaction -> 1
        }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): RecyclerView.ViewHolder = when (viewType) {
        0 -> object : RecyclerView.ViewHolder(TextView(parent.context)) {}
        1 -> TransactionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false))
        else -> throw AssertionError()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>,
    ) {
        when (val t = getItem(position)) {
            is Item.Day -> (holder.itemView as TextView).text = t.day.toString()
            is Item.Transaction -> bind(holder as TransactionViewHolder, t, payloads)
        }
    }

    private fun bind(holder: TransactionViewHolder, t: Item.Transaction, payloads: List<Any>) {
        val changes =
            if (payloads.isEmpty()) emptySet<ChangeField>()
            else EnumSet.noneOf(ChangeField::class.java).also { changes ->
                payloads.forEach { payload ->
                    (payload as? Collection<*>)?.filterIsInstanceTo(changes)
                }
            }

        holder.apply {
            if (changes.isEmpty()) {
                account.text = t.account
            }
            if (changes.isEmpty() || ChangeField.RECEIVER in changes) {
                receiver.text = t.receiver
            }
            if (changes.isEmpty() || ChangeField.AMOUNT in changes) {
                amount.text = "$%.2f".format(t.amount / 100f)
            }
            if (changes.isEmpty() || ChangeField.STATUS in changes) {
                status.text = t.status.name
                status.setTextColor(when (t.status) {
                    Item.Transaction.Status.PROCESSING -> Color.DKGRAY
                    Item.Transaction.Status.SUCCESSFUL -> Color.GREEN
                    Item.Transaction.Status.FAILED -> Color.RED
                })
            }
        }
    }

    fun add(transaction: Item, callback: Runnable) {
        submitList(listOf(transaction) + currentList, callback)
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiver = view.findViewById<TextView>(R.id.receiver_tv)
        val account = view.findViewById<TextView>(R.id.account_tv)
        val amount = view.findViewById<TextView>(R.id.amount_tv)
        val status = view.findViewById<TextView>(R.id.status_tv)
    }
}
package com.example.hs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionsAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        var transaction = transactions[position]

        holder.receiver.text = transaction.receiver
        holder.account.text = transaction.account
        holder.amount.text = transaction.transactionAmount
        holder.status.text = transaction.transactionStatus
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiver = view.findViewById<TextView>(R.id.receiver_tv)
        val account = view.findViewById<TextView>(R.id.account_tv)
        val amount = view.findViewById<TextView>(R.id.amount_tv)
        val status = view.findViewById<TextView>(R.id.status_tv)
    }
}
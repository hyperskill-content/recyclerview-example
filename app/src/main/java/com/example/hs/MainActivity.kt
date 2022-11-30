package com.example.hs

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.time.LocalDate
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private lateinit var tabs: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val swipe2refresh = findViewById<SwipeRefreshLayout>(R.id.swipe2refresh)
        val fab = findViewById<FloatingActionButton>(R.id.add)

        tabs = findViewById(R.id.tabs)
        val selectedTabIndex = savedInstanceState?.getInt("tab") ?: 0
        tabs.getTabAt(selectedTabIndex)!!.select()
        selectTabAt(selectedTabIndex, recyclerView, swipe2refresh, fab)
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTabAt(tab.position, recyclerView, swipe2refresh, fab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun selectTabAt(
        tabAt: Int, recyclerView: RecyclerView, swipe2refresh: SwipeRefreshLayout, fab: FloatingActionButton,
    ) {
        when (tabAt) {
            0 -> showTransactions(recyclerView, swipe2refresh, fab)
            1 -> showServices(recyclerView, swipe2refresh, fab)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tab", tabs.selectedTabPosition)
    }

    private fun showTransactions(
        recyclerView: RecyclerView,
        swipe2refresh: SwipeRefreshLayout,
        fab: FloatingActionButton
    ) {
        fab.visibility = View.VISIBLE
        swipe2refresh.isEnabled = true

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setPadding(0, 0, 0, 0)

        val bank = Bank()
        val adapter = TransactionsAdapter(bank.generateTransactions().withDates())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider)!!)
        })

        fab.setOnClickListener {
            val oldList = adapter.currentList.filterIsInstance<Item.Transaction>()
            adapter.submitList((listOf(bank.generateTransaction()) + oldList).withDates()) {
                recyclerView.scrollToPosition(0)
            }
        }

        swipe2refresh.setOnRefreshListener {
            val oldList = adapter.currentList.filterIsInstance<Item.Transaction>()
            adapter.submitList(bank.update(oldList).withDates()) {
                swipe2refresh.isRefreshing = false
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun List<Item.Transaction>.withDates(): List<Item> {
        var lastDay: LocalDate? = null
        val items = ArrayList<Item>()
        val zone = ZoneId.systemDefault()
        forEach {
            val time = it.time.atZone(zone).toLocalDate()
            if (lastDay != time) {
                lastDay = time.also { items.add(Item.Day(it)) }
            }
            items.add(it)
        }
        return items
    }


    private fun showServices(recyclerView: RecyclerView, swipe2refresh: SwipeRefreshLayout, fab: FloatingActionButton) {
        fab.visibility = View.GONE
        repeat(recyclerView.itemDecorationCount) { recyclerView.removeItemDecorationAt(0) }
        swipe2refresh.isEnabled = false

        recyclerView.apply {
            (4 * resources.displayMetrics.density).toInt().let { setPadding(it, it, it, it) }
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                private val items = arrayOf(
                    "💳 🤑\nnew card and account",
                    "🏧\naddresses",
                    "☂\nlife and health insurance",
                    "️🏠\nmortgage",
                    "📈\nstocks and investments",
                    "👩‍💻\naccounting"
                ).map {
                    SpannableStringBuilder(it).apply {
                        setSpan(RelativeSizeSpan(1.5f), 0, it.indexOf('\n'), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                override fun getItemCount(): Int =
                    items.size

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                    object : RecyclerView.ViewHolder(
                        TextView(parent.context).apply {
                            val dp = resources.displayMetrics.density
                            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                                (4 * dp).toInt().let {
                                    leftMargin = it; rightMargin = it
                                    topMargin = it; bottomMargin = it
                                }
                            }
                            textSize = 32f
                            setTextColor(Color.BLACK)
                            background = GradientDrawable().apply {
                                setStroke((2 * dp).toInt(), 0x883377FF.toInt())
                                cornerRadius = 8 * dp
                            }
                            (8 * dp).toInt().let { setPadding(it, it, it, it) }
                        }
                    ) {}

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    (holder.itemView as TextView).text = items[position]
                }

            }
        }
    }

}

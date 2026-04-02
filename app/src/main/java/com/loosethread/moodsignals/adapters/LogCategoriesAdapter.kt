package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.ItemDayCategoriesLogBinding
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.LogCategory
import com.loosethread.moodsignals.views.Chart

class LogCategoriesAdapter(
    private val dayId: Int,
    private val categories: MutableList<LogCategory>
) : RecyclerView.Adapter<LogCategoriesAdapter.LogCategoriesViewHolder>() {

    inner class LogCategoriesViewHolder(private val logCategoriesBinding: ItemDayCategoriesLogBinding) : RecyclerView.ViewHolder(logCategoriesBinding.root) {
        private var expanded = false

        fun bind(category: LogCategory, position: Int) {
            logCategoriesBinding.tvCategoryName.text = category.description
            logCategoriesBinding.tvCategoryName.background = Chart(
                logCategoriesBinding.root.context,
                intArrayOf(
                    category.score_count[1]?: 0,
                    category.score_count[2]?: 0,
                    category.score_count[3]?: 0
                )
            )
            logCategoriesBinding.tvCategoryName.setOnClickListener {
                if (expanded) {
                    logCategoriesBinding.rvSignals.visibility = View.GONE
                    expanded = false
                } else {
                    logCategoriesBinding.rvSignals.visibility = View.VISIBLE
                    expanded = true
                }
            }

            val daySignalScores = Db.getDaySignalValuesByCategory(dayId, category.categoryId)
            val logSignalAdapter = LogSignalAdapter(daySignalScores)
            logCategoriesBinding.rvSignals.adapter = logSignalAdapter
            logCategoriesBinding.rvSignals.layoutManager =
                FullWidthLinearLayoutManager(logCategoriesBinding.root.context, LinearLayoutManager.VERTICAL, false)

            val dividerItemDecoration = DividerItemDecoration(logCategoriesBinding.root.context, LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(logCategoriesBinding.root.context.getDrawable(R.drawable.blankline)!!)
            logCategoriesBinding.rvSignals.addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogCategoriesViewHolder {
        val logSignalBinding = ItemDayCategoriesLogBinding.inflate(LayoutInflater.from(parent.context))
        return LogCategoriesViewHolder(logSignalBinding)
    }

    override fun onBindViewHolder(holder: LogCategoriesViewHolder, position: Int) {
        val currentCategory = categories[position]
        holder.bind(currentCategory, position)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
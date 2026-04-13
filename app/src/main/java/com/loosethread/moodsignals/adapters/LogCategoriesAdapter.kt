package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.ItemDayCategoriesLogBinding
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.LogCategory
import com.loosethread.moodsignals.views.Chart

class LogCategoriesAdapter(
    private val dayId: Int,
    private val categories: MutableList<LogCategory>
) : RecyclerView.Adapter<LogCategoriesAdapter.LogCategoriesViewHolder>() {

    inner class LogCategoriesViewHolder(private val binding: ItemDayCategoriesLogBinding) : RecyclerView.ViewHolder(binding.root) {
        private var expanded = false

        fun bind(category: LogCategory, position: Int) {
            binding.tvCategoryName.text = category.description
            binding.tvCategoryName.background = Chart(
                binding.root.context,
                intArrayOf(
                    category.score_count[1]?: 0,
                    category.score_count[2]?: 0,
                    category.score_count[3]?: 0,
                ),
                Chart.ROUNDED_ALL
            )
            binding.tvCategoryName.setOnClickListener {
                if (expanded) {
                    binding.rvSignals.visibility = View.GONE
                    (binding.tvCategoryName.background as Chart).setStyle(Chart.ROUNDED_ALL)
                    expanded = false
                } else {
                    binding.rvSignals.visibility = View.VISIBLE
                    (binding.tvCategoryName.background as Chart).setStyle(Chart.ROUNDED_TOP)
                    expanded = true
                }
            }

            val daySignalScores = Db.getDaySignalValuesByCategory(dayId, category.categoryId)
            val logSignalAdapter = LogSignalAdapter(daySignalScores)
            binding.rvSignals.adapter = logSignalAdapter
            binding.rvSignals.layoutManager =
                FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

            val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(binding.root.context.getDrawable(R.drawable.blankline)!!)
            binding.rvSignals.addItemDecoration(dividerItemDecoration)
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
package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemCategoryBinding
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.dialogs.DeleteCategoryDialog
import com.loosethread.moodsignals.dialogs.EditCategoryDialog

class CategoryAdapter(
    private val categories: MutableList<SignalCategory>,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CategoryAdapter.SignalCategoryViewHolder>() {

    inner class SignalCategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: SignalCategory, position: Int) {
            binding.tvTitle.setText(category.description)

            val requestKeyEdit = "edit_comment_${category.id}"

            binding.tvTitle.setOnClickListener {
                val dialog = EditCategoryDialog()
                dialog.arguments = bundleOf(
                    "requestKey" to requestKeyEdit,
                    "description" to category.description
                )
                dialog.show(fragmentManager, "EditCategoryFragment${category.id}")
            }

            fragmentManager.setFragmentResultListener(
                requestKeyEdit,
                lifecycleOwner) { _, bundle ->
                val updated = bundle.getBoolean("isUpdated", false)
                if (updated) {
                    val description = bundle.getString("description")
                    description?.isNullOrEmpty()?.let {
                        if(!it) {
                            Db.updateCategory(category.id!!, description)
                            categories[position].description = description
                            notifyItemChanged(position)
                        }
                    }
                }
            }

            if (categories.size == 1) {
                binding.ibDelete.visibility = ImageButton.GONE
            } else {
                val requestKeyDelete = "delete_comment_${category.id}"

                binding.ibDelete.setOnClickListener {
                    val dialog = DeleteCategoryDialog()
                    dialog.arguments = bundleOf(
                        "requestKey" to requestKeyDelete,
                        "id" to category.id
                    )
                    dialog.show(fragmentManager, "DeleteCategoryFragment${category.id}")
                }

                fragmentManager.setFragmentResultListener(
                    requestKeyDelete,
                    lifecycleOwner) { _, bundle ->
                    val deleted = bundle.getBoolean("isDeleted", false)
                    if (deleted) {
                        val replacement = bundle.getInt("replacement")
                        Db.deleteCategory(category.id!!, replacement)
                        categories.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalCategoryViewHolder {
        val notificationTimeBinding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return SignalCategoryViewHolder(notificationTimeBinding)
    }

    override fun onBindViewHolder(holder: SignalCategoryViewHolder, position: Int) {
        val currentSignalCategory = categories[position]
        if (currentSignalCategory != null)
            holder.bind(currentSignalCategory, position)
    }

    fun addCategory(category: SignalCategory) {
        categories.add(category)
        notifyItemInserted(categories.size - 1)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
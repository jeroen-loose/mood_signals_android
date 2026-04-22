package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemCommentSearchBinding

class CommentSearchAdapter(
    private var comments: MutableMap<Int, String>
) : RecyclerView.Adapter<CommentSearchAdapter.CommentSearchViewHolder>() {
    var onDaySelected: ((dayId: Int) -> Unit) ?= null

    inner class CommentSearchViewHolder(private val binding: ItemCommentSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dayId: Int, comment: String, position: Int) {
            binding.tvComment.text = comment
            binding.tvComment.setOnClickListener {
                onDaySelected?.invoke(dayId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentSearchViewHolder {
        val binding = ItemCommentSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentSearchViewHolder, position: Int) {
        val dayId = comments.keys.elementAt(position)
        val currentComment = comments[dayId] ?: ""
        holder.bind(dayId, currentComment, position)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(newComments: MutableMap<Int, String>) {
        if (comments != newComments) {
            comments = newComments
            notifyDataSetChanged()
        }
    }
}
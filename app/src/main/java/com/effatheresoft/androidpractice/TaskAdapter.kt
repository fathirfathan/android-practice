package com.effatheresoft.androidpractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effatheresoft.androidpractice.databinding.TaskItemBinding

class TaskAdapter(): ListAdapter<TaskResponse, TaskAdapter.TaskHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<TaskResponse>() {
            override fun areItemsTheSame(
                oldItem: TaskResponse,
                newItem: TaskResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TaskResponse,
                newItem: TaskResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskHolder {
        val binding = TaskItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return TaskHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TaskHolder,
        position: Int
    ) {
        holder.binding.textViewTask.text = getItem(position).title
    }

    class TaskHolder(val binding: TaskItemBinding):
        RecyclerView.ViewHolder(binding.root)
}

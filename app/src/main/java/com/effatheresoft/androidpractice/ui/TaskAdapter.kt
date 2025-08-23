package com.effatheresoft.androidpractice.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effatheresoft.androidpractice.data.local.TaskEntity
import com.effatheresoft.androidpractice.databinding.TaskItemBinding
import com.effatheresoft.androidpractice.ui.details.Task

class TaskAdapter: ListAdapter<Task, TaskAdapter.TaskHolder>(DiffCallback) {
    var onItemClicked: ((Task) -> Unit)? = null

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
        holder.binding.textViewTask.setOnClickListener { onItemClicked?.invoke(getItem(position)) }
    }

    class TaskHolder(val binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root)



    private companion object DiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean {
            return oldItem == newItem
        }
    }
}
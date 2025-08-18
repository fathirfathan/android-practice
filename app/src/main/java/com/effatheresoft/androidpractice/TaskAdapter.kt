package com.effatheresoft.androidpractice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(val tasks: ArrayList<String>): RecyclerView.Adapter<TaskAdapter.TaskHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskHolder {
        val taskView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskHolder(taskView)
    }

    override fun onBindViewHolder(
        holder: TaskHolder,
        position: Int
    ) {
        holder.textViewTask.text = tasks[position]
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    class TaskHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTask: TextView = view.findViewById(R.id.textViewTask)
    }
}

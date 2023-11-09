package com.example.todolist

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewTaskHandlerAdapter(
    var context: Context,
    var array: MutableList<TasksItemView>,
    private val instance: Fragment
): RecyclerView.Adapter<RecyclerViewTaskHandlerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater:LayoutInflater = LayoutInflater.from(context)

        val view = layoutInflater.inflate(R.layout.completed_tasks_handler,parent,false)

        var imageView:ImageView = view.findViewById(R.id.checking)

        if(instance is InCompleteTasks) {
            imageView.setImageResource(R.drawable.cross)
        }else{
            imageView.setImageResource(R.drawable.tick_)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskView.text = array[position].title
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskView:TextView

        init {
            taskView = itemView.findViewById(R.id.completed_title)
            taskView.paintFlags = taskView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

    }
}
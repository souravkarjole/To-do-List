package com.example.todolist

interface ClickToAddTasksInterface {
    fun setOnClickListenerToAddTask(completeOrInComplete: Boolean)
    fun setOnClickListenerToDeleteTask()
}

interface ShowDeletedMenu {
    fun showDeletedMenu(show: Boolean)
}
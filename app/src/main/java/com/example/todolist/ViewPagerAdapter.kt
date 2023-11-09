package com.example.todolist

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
): FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return (when(position){
            0 -> {
                AllTasks()
            }
            1 -> {
                CompletedTasks()
            }
            2 -> {
                InCompleteTasks()
            }
            else -> {
                TaskOverView()
            }
        })
    }

}
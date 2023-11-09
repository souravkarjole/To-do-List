package com.example.todolist

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DataBase.DataBase


/**
 * A simple [Fragment] subclass.
 * Use the [CompletedTasks.newInstance] factory method to
 * create an instance of this fragment.
**/


class CompletedTasks : Fragment() {

    lateinit var recyclerView:RecyclerView
    lateinit var array:MutableList<TasksItemView>
    lateinit var dataBase: DataBase
    lateinit var nullTasks: ImageView
    lateinit var nullTasks2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBase = DataBase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_completed_tasks, container, false)

        recyclerView = view.findViewById(R.id.completedNotesRecyclerView)
        nullTasks = view.findViewById(R.id.NullTasks)
        nullTasks2 = view.findViewById(R.id.NullTasks2)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        array = arrayListOf()
        displayCompletedTasks()
        newDataCompleted(false)

        return view
    }

    override fun onResume() {
        super.onResume()

        if (isNewDataCompleted()) {
            array = arrayListOf()
            displayCompletedTasks()
        }
        newDataCompleted(false)
    }

    private fun displayCompletedTasks(){
        val cursor:Cursor = dataBase.readCompletedTask()
        if (cursor.count == 0){
            nullTasks.visibility = View.VISIBLE
            nullTasks2.visibility = View.VISIBLE
        }else{
            nullTasks.visibility = View.GONE
            nullTasks2.visibility = View.GONE
            while (cursor.moveToNext()){
                array.add(TasksItemView("0",cursor.getString(1),""))
            }

            var recyclerViewCompletedNotesAdapter = RecyclerViewTaskHandlerAdapter(requireContext(),array,CompletedTasks())

            recyclerView.adapter = recyclerViewCompletedNotesAdapter
        }
    }

    companion object {
        private var boolean:Boolean = true


        fun newDataCompleted(boolean: Boolean){
            this.boolean = boolean
        }

        fun isNewDataCompleted():Boolean{
            return boolean
        }
    }
}
package com.example.todolist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.todolist.DataBase.DataBase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Calendar
import kotlin.math.abs


class AllTasks : Fragment(),ClickToAddTasksInterface,ShowDeletedMenu {

    lateinit var recyclerView: RecyclerView
    lateinit var array: MutableList<TasksItemView>
    lateinit var recyclerViewAdapter:RecyclerViewAddNotesAdapter
    lateinit var addNote: FloatingActionButton
    lateinit var db: DataBase
    lateinit var animation:LottieAnimationView
    lateinit var nulltasks:ImageView
    lateinit var nulltasks2:TextView
    lateinit var mainMenu: Menu
    private var isDialogOpen = false


    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DataBase(requireContext())
        array = arrayListOf()
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferencesFile",Context.MODE_PRIVATE)

        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_all_tasks, container, false)
        recyclerView = view.findViewById(R.id.allNotesRecyclerView)
        addNote = view.findViewById(R.id.addNote)
        animation = view.findViewById(R.id.lottie_completed_task)
        nulltasks = view.findViewById(R.id.NullTasks)
        nulltasks2 = view.findViewById(R.id.NullTasks2)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)

        totalCompletedTasks = sharedPreferences.getInt("completedCount",0)
        totalInCompleteTasks = sharedPreferences.getInt("inCompleteTasks",0)


        array = arrayListOf()


        displayTasks()

        newDataAdded(false)

        swipeFeature()

        addNote.setOnClickListener {
            if(!isDialogOpen){
                isDialogOpen = true
                showBottomSheetDialog()
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mainMenu = menu
        inflater.inflate(R.menu.menu,mainMenu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.just_delete -> {
                val alertDialogBuild:AlertDialog.Builder = AlertDialog.Builder(context)
                val view:View = layoutInflater.inflate(R.layout.alert_dialog,null)

                alertDialogBuild.setView(view)
                val alertDialog = alertDialogBuild.create()

                alertDialog.show()
                alertDialog.window?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val cancel:TextView = view.findViewById(R.id.cancel)
                val delete:TextView = view.findViewById(R.id.delete)

                delete.setOnClickListener {
                    recyclerViewAdapter.onDeleteTasks()

                    if(array.size == 0){
                        totalPendingTasks = 0
                        nulltasks.visibility = View.VISIBLE
                        nulltasks2.visibility = View.VISIBLE
                    }
                    alertDialog.dismiss()
                    showDeletedMenu(false)

                    totalPendingTasks = if(totalPendingTasks > 0) totalPendingTasks-1 else 0
                }
                cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            R.id.cancel -> {
                recyclerViewAdapter.setDefault()
                showDeletedMenu(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBottomSheetDialog() {
        var dialog:Dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.dialog_layout)

        val createNew:LinearLayout = dialog.findViewById(R.id.createNew)
        val useTemplate:LinearLayout = dialog.findViewById(R.id.useTemplate)

        createNew.setOnClickListener {
            startActivity(Intent(requireActivity(),AddTasks::class.java))
            animation.visibility = View.GONE
            animation.pauseAnimation()
            dialog.dismiss()
        }

        useTemplate.setOnClickListener {
            startActivity(Intent(requireActivity(),Template::class.java))
            dialog.dismiss()
        }
        isDialogOpen = false
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onResume() {
        super.onResume()

        if(isNewDataAdded()) {
            array = arrayListOf()
            displayTasks()
        }

        newDataAdded(false)
    }

    override fun onPause() {
        super.onPause()
        animation.visibility = View.GONE
        animation.pauseAnimation()

        if(array.size == 0){
            totalPendingTasks = 0
            nulltasks.visibility = View.VISIBLE
            nulltasks2.visibility = View.VISIBLE
        }
    }

    private fun displayTasks(){
        val cursor1:Cursor = db.readTask()

        if(cursor1.count != 0){
            nulltasks.visibility = View.GONE
            nulltasks2.visibility = View.GONE

            while (cursor1.moveToNext()){
                var cursor2 = db.getUpcomingTask(cursor1.getString(2))

                if(cursor2 != null && cursor2.moveToFirst()) {
                    var type = cursor2.getString(6)

                    when(type){
                        "daily","custom","once","once_allDay" -> {
                            var requestId = cursor2.getInt(0)
                            var date = cursor2.getInt(1)
                            var month = cursor2.getInt(2)
                            var year = cursor2.getInt(3)
                            var hour = cursor2.getInt(4)
                            var minute = cursor2.getInt(5)

                            array.add(
                                TasksItemView(
                                    cursor1.getString(0),
                                    cursor1.getString(1),
                                    requestId,
                                    cursor1.getString(2),
                                    date,
                                    month,
                                    year,
                                    hour,
                                    minute,
                                    type
                                )
                            )
                        }
                    }
                    cursor2.close()
                }else{
                    array.add(TasksItemView(cursor1.getString(0),cursor1.getString(1),cursor1.getString(2)))
                }
            }
        }

       // same as comparator interface in java, prefer GFG & my notes
        array.sortWith(compareBy(
            { it.year },
            { it.month },
            { it.date },
            { it.minute },
            { it.hour }
        ))

        recyclerViewAdapter = RecyclerViewAddNotesAdapter(
            requireContext(),
            array,
            this,
            this,
            recyclerView
        )

        totalPendingTasks = array.size
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        recyclerView.adapter = recyclerViewAdapter
    }


    private fun swipeFeature() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        position = viewHolder.bindingAdapterPosition
                        setOnClickListenerToAddTask(false)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val alpha = 1.0f - abs(dX) / itemView.width.toFloat()

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    itemView.alpha = alpha
                }

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightActionIcon(R.drawable.uncomplete_tasks)
                    .addSwipeRightLabel("undone")
                    .setSwipeRightLabelTextSize(0,45f)
                    .create()
                    .decorate()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
        private var boolean:Boolean = true
        var position = 0
        var totalPendingTasks:Int = 0
        var totalCompletedTasks:Int = 0
        var totalInCompleteTasks:Int = 0
        var autoGestures:Boolean = true

        fun newDataAdded(boolean: Boolean){
            this.boolean = boolean
        }

        fun isNewDataAdded():Boolean{
            return boolean
        }
    }

    override fun setOnClickListenerToAddTask(completeOrInComplete: Boolean) {
        isTaskCompleted(completeOrInComplete)

        when(array[position].type){
            "once","once_allDay" -> {
                db.deleteTasks(array[position].uniqueId)
                db.deleteDueTasks(array[position].uniqueId)
                array.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)
            }
            "daily","custom"-> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()

                calendar.set(Calendar.DATE,array[position].date)
                calendar.set(Calendar.YEAR,array[position].year)
                calendar.set(Calendar.MONTH,array[position].month)

                calendar.add(Calendar.DAY_OF_YEAR, 7)
//                Log.e("TAG", "${array[position].date}:${array[position].month} ", )
                db.updateDueDate(calendar.get(Calendar.DATE),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR),array[position].requestId)

                var cursor2 = db.getUpcomingTask(array[position].uniqueId)

                if(cursor2.moveToFirst()) {
                    array[position].requestId = cursor2.getInt(0)
                    array[position].date = cursor2.getInt(1)
                    array[position].month = cursor2.getInt(2)
                    array[position].year = cursor2.getInt(3)
                }
//                Log.e("TAG", "${array[position].date}:${array[position].month} ", )
                recyclerViewAdapter.notifyItemChanged(position)
                totalPendingTasks++
            }
            else -> {
                db.deleteTasks(array[position].uniqueId)
                array.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)
            }
        }

        if(array.size == 0) {
            animation.visibility = View.VISIBLE
            animation.playAnimation()
            Toast.makeText(context,"Tasks Completed Successfully!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun isTaskCompleted(boolean: Boolean){
        if(boolean){
            CompletedTasks.newDataCompleted(true)
            db.addCompletedTask(array[position].title)
            updateTheCompletedTasksCount()
        }else{
            InCompleteTasks.newDataDeleted(true)
            db.addInCompleteTask(array[position].title)
            updateTheInCompletedTasksCount()
        }
    }

    override fun setOnClickListenerToDeleteTask() {

    }

    override fun showDeletedMenu(show: Boolean) {
        mainMenu.findItem(R.id.just_delete).isVisible = show
        mainMenu.findItem(R.id.cancel).isVisible = show
    }

    private fun updateTheCompletedTasksCount(){
        totalCompletedTasks++
        totalPendingTasks = if(totalPendingTasks > 0) totalPendingTasks-1 else 0

        val editor = sharedPreferences.edit()
        editor.putInt("completedCount", totalCompletedTasks)
        editor.apply()
    }

    private fun updateTheInCompletedTasksCount(){
        totalInCompleteTasks++
        totalPendingTasks = if(totalPendingTasks > 0) totalPendingTasks-1 else 0
        val editor = sharedPreferences.edit()
        editor.putInt("inCompleteTasks", totalInCompleteTasks)
        editor.apply()
    }
}
package com.example.todolist

import android.animation.Animator
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.todolist.DataBase.DataBase
import java.util.Calendar

class RecyclerViewAddNotesAdapter(
    private val context: Context,
    private val array: MutableList<TasksItemView>,
    private val clickToAddTasksInterface: ClickToAddTasksInterface,
    private val showDeletedMenu: ShowDeletedMenu,
    private val recyclerView: RecyclerView
    ): RecyclerView.Adapter<RecyclerViewAddNotesAdapter.ViewHolder>() {

    var months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    var days = arrayOf("","sun","mon","tue","wed","thur","fri","sat")

    private var calendar:Calendar = Calendar.getInstance()
    private var isEnabled:Boolean = false
    private var selectDeletedItems:MutableList<Int> = arrayListOf()
    private var selectedCheckBoxes:MutableList<ViewHolder> = arrayListOf()
    private var vibrator:Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private lateinit var item:TasksItemView
    private var todayDate:Int = 0
    val todayCalendar = Calendar.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.custom_task_view,parent,false)

        calendar.timeInMillis = System.currentTimeMillis()

        return ViewHolder(view, clickToAddTasksInterface,showDeletedMenu)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    fun setDefault(){
        selectedCheckBoxes.forEach { it ->
            it.checkBox.isSelected = false
        }
        if(selectDeletedItems.isNotEmpty() || selectedCheckBoxes.isNotEmpty()) {
            selectDeletedItems.clear()
            selectedCheckBoxes.clear()
            isEnabled = false
        }
    }


    fun setFadingAnimation(itemView: View) {
        itemView.alpha = 0f
        itemView.animate()
            .alpha(1f)
            .setDuration(700) // Adjust the duration as needed
            .start()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setFadingAnimation(holder.itemView)
        todayCalendar.timeInMillis = System.currentTimeMillis()
        todayDate = todayCalendar.get(Calendar.DATE)

        val date = array[position].date
        val month = array[position].month
        val year = array[position].year
        val hour:Int = array[position].hour
        val minute:Int = array[position].minute
        val type:String = array[position].type


        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DATE,date)
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)


        var sd:String
        if(type == "once_allDay") {
            calendar.add(Calendar.DAY_OF_YEAR,1)
            sd = calculateTime(holder, date, month, year, dayOfWeek, hour)
        }else{
            sd = "${calculateTime(holder, date, month, year, dayOfWeek, hour)}, " + getTime(calendar)
        }

        holder.dateTimeTextView.text = sd
        holder.titleTextView.text = array[position].title


        holder.itemView.alpha = 0f // Initially set alpha to 0



        if(type == "once" && array[position].requestId == -1) holder.dateTimeTextView.text = "";  // because tasks has no due date & no time

        // when user click on edit
        holder.edit.setOnClickListener {
            var intent:Intent = Intent(context,UpdateTasks::class.java)
            intent.putExtra("title",array[position].title)
            intent.putExtra("id",array[position].id)

            context.startActivity(intent)
        }

        // when user clicks on checkBox
        holder.checkBox.setOnLongClickListener {
            if(!isEnabled) {
                isEnabled = true
                holder.showDeletedMenu.showDeletedMenu(true)
                holder.checkBox.isSelected = true
                selectDeletedItems.add(position)
                selectedCheckBoxes.add(holder)
            }
            true
        }

        holder.checkBox.setOnClickListener {
            if(!isEnabled) {
                holder.animation.visibility = View.VISIBLE
                holder.animation.playAnimation()
                vibrator.vibrate(25)

                // after animation (tick) ends, call ClickToAddTasksInterface interface to set the position and invoke its functions
                holder.animation.removeAllAnimatorListeners()

                holder.animation.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}

                    override fun onAnimationEnd(p0: Animator) {
                        holder.animation.pauseAnimation()
                        holder.animation.visibility = View.GONE
                        AllTasks.position = holder.bindingAdapterPosition
                        cancelSingleAlarm(context,array[holder.bindingAdapterPosition].requestId)
                        holder.clickToAddTasksInterface.setOnClickListenerToAddTask(true)
                    }
                })
            }else{
                if (selectDeletedItems.contains(position)){
                    holder.checkBox.isSelected = false
                    selectDeletedItems.remove(position)
                    selectedCheckBoxes.remove(holder)
                    if(selectDeletedItems.isEmpty()){
                        isEnabled = false
                        holder.showDeletedMenu.showDeletedMenu(false)
                    }
                }else{
                    isEnabled = true
                    holder.checkBox.isSelected = true
                    selectDeletedItems.add(position)
                    selectedCheckBoxes.add(holder)
                }
            }
        }
    }


    private fun calculateTime(holder: ViewHolder,date:Int,month:Int,year:Int,dayOfWeek:Int,hour:Int): String {
        var result = if(isTaskForTodayOrTomorrow(holder,date,month,year,hour)){
            Log.e("TAG", "$date: $todayDate ", )
            if(date == todayDate) "today" else "tomorrow"
        }else{
            "${days[dayOfWeek]}, " + "${date}-${months[month]}-${year}"
        }

        return result
    }

    private fun isTaskForTodayOrTomorrow(holder: ViewHolder,date: Int, month: Int, year: Int,hour:Int): Boolean {
        var millis = todayCalendar.timeInMillis
        todayCalendar.set(Calendar.SECOND,0)

        var today = todayCalendar.get(Calendar.DATE)
        var todayMonth = todayCalendar.get(Calendar.MONTH)
        var todayYear = todayCalendar.get(Calendar.YEAR)

        if (year < todayYear || (year == todayYear && (month < todayMonth || (month == todayMonth && date < today)))) {
            holder.dateTimeTextView.setTextColor(Color.parseColor("#CF2727"));
            return false
        }

        if(calendar.timeInMillis <= millis){
            holder.dateTimeTextView.setTextColor(Color.parseColor("#CF2727"));
        }else {
            holder.dateTimeTextView.setTextColor(Color.WHITE);
        }

        todayCalendar.add(Calendar.DATE,1)

        val tomorrow = todayCalendar.get(Calendar.DATE)
        val tomorrowMonth = todayCalendar.get(Calendar.MONTH)
        val tomorrowYear = todayCalendar.get(Calendar.YEAR)

        return ((today == date && todayMonth == month && todayYear == year) ||
                (tomorrow == date && tomorrowMonth == month && tomorrowYear == year))
    }

    // format the time & return
    private fun getTime(calendar: Calendar): String {
        var hour = calendar.get(Calendar.HOUR)

        if(hour == 0){
            hour = 12
        }
        return ("${hour}:${String.format("%02d",calendar.get(Calendar.MINUTE))} ${if (calendar.get(Calendar.AM_PM) == 0) "am" else "pm"}")
    }


    fun onDeleteTasks(){
        val dataBase: DataBase = DataBase(context)
        val itemsToDelete = ArrayList<TasksItemView>()

        for(i in 0..< selectDeletedItems.size){
            val uniqueId = array[selectDeletedItems[i]].uniqueId
            val cursor:Cursor = dataBase.getTypeOfDueTasks(uniqueId)

            when(array[selectDeletedItems[i]].type){
                "once","once_allDay" -> {

                    if(cursor.moveToNext()) {
                        cancelSingleAlarm(context, cursor.getInt(1))
                    }

                    dataBase.deleteTasks(uniqueId)
                    dataBase.deleteDueTasks(uniqueId)
                }
                "daily","custom" -> {

                    while(cursor.moveToNext()) {
                        cancelAlarm(context, cursor.getInt(1))
                    }
                    dataBase.deleteTasks(uniqueId)
                    dataBase.deleteDueTasks(uniqueId)
                }
                else -> {
                    dataBase.deleteTasks(uniqueId)
                }
            }
            itemsToDelete.add(array[selectDeletedItems[i]])
        }

        for (item in itemsToDelete) {
            selectDeletedItems.remove(array.indexOf(item))
            array.remove(item)
        }

        // Notify the adapter
        notifyDataSetChanged()
        setDefault()
    }

    private fun cancelAlarm(context: Context, requestId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyReceiver::class.java)
        intent.putExtra("requestId",requestId)
        intent.action = "DAILY_ALARM_ACTION"

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestId,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun cancelSingleAlarm(context: Context, requestId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyReceiver::class.java)
        intent.action = "SINGLE_ALARM"

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestId,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
   inner class ViewHolder(
        itemView: View,
        clickToAddTasksInterface: ClickToAddTasksInterface,
        showDeletedMenu: ShowDeletedMenu
    ) :RecyclerView.ViewHolder(itemView) {
        var titleTextView:TextView
        var dateTimeTextView:TextView
        var edit:LinearLayout
        var checkBox:RelativeLayout
        var animation:LottieAnimationView
        var clickToAddTasksInterface:ClickToAddTasksInterface
        var showDeletedMenu: ShowDeletedMenu


        init {
            titleTextView = itemView.findViewById(R.id.task_title)
            dateTimeTextView = itemView.findViewById(R.id.dateAndTime)
            edit = itemView.findViewById(R.id.edit_query)
            checkBox = itemView.findViewById(R.id.checkbox)
            animation = itemView.findViewById(R.id.animation)

            this.clickToAddTasksInterface = clickToAddTasksInterface  // initializing ClickToAddTasksInterface interface
            this.showDeletedMenu = showDeletedMenu
        }
    }
}

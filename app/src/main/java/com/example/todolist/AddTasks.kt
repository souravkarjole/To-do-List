package com.example.todolist

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.todolist.DataBase.DataBase
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

class AddTasks : AppCompatActivity() {
    lateinit var title:TextInputEditText
    lateinit var calendarTextView:EditText
    lateinit var calendarImageView: ImageView
    lateinit var cancelDateImageView: ImageView
    lateinit var create:Button
    lateinit var sharedPreferences: SharedPreferences

    var months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    var days = arrayOf("","sun","mon","tue","wed","thur","fri","sat","sun")
    var todayDate:Int = 0


    var isRepeated = false
    var allDay = true

    var hour:Int = 0
    var minute:Int = 0
    val calendar: Calendar = Calendar.getInstance()
    val dataBase:DataBase = DataBase(this)


    // for dialog layout
    private lateinit var timePicker: TimePicker
    private lateinit var switchCompat: SwitchCompat
    private lateinit var sunday:TextView
    private lateinit var monday:TextView
    private lateinit var tuesday:TextView
    private lateinit var wednesday:TextView
    private lateinit var thursday:TextView
    private lateinit var friday:TextView
    private lateinit var saturday:TextView
    private lateinit var hide1:LinearLayout
    private lateinit var buttonDone: TextView
    private lateinit var buttonCancel: TextView

    private lateinit var noTime:TextView
    private lateinit var first:TextView
    private lateinit var second:TextView
    private lateinit var third:TextView
    private lateinit var fourth:TextView
    private lateinit var fifth:TextView
    private lateinit var sixth:TextView

    private lateinit var dayViews:Array<TextView>

    private var isTimePickerDialogOpen = false
    private var isDatePickerDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)
        setMainUi() // ui

        create.setOnClickListener {
            if(validation()) {
                AllTasks.newDataAdded(true)

                if(calendarTextView.text.isEmpty()){
                    // once but no due date
                    setOnceWithNoDue()
                }else {
                    // daily
                    if (isRepeated) {
                        val daysList = selectedDays()
                        setDailyAlarm(daysList, listOf(hour), listOf(minute), title.text.toString())
                    } else {
                        // once with due date
                        setAlarm(title.text.toString())
                    }

                }
                onBackPressed()
            }
        }

        calendarImageView.setOnClickListener {
            if(!isDatePickerDialogOpen) {
                isDatePickerDialogOpen = true
                showDialogDatePicker()
            }
        }

        calendarTextView.setOnClickListener {
            if(!isDatePickerDialogOpen) {
                isDatePickerDialogOpen = true
                showDialogDatePicker()
            }
        }
    }

    // dialog pickers
    private fun showDialogDatePicker() {

        val dialog: Dialog = Dialog(this)

        dialog.setContentView(R.layout.date_picker_layout)

        val calendarView: CalendarView = dialog.findViewById(R.id.datePicker)
        val setTimePicker: LinearLayout = dialog.findViewById(R.id.setTime)
        val getTime: TextView = dialog.findViewById(R.id.getTime)
        val getRepeatable : TextView = dialog.findViewById(R.id.getRepeat)
        val cancel: TextView = dialog.findViewById(R.id.cancel)
        val done: TextView = dialog.findViewById(R.id.done)

        todayDate = calendar.get(Calendar.DATE)
        calendarView.date = calendar.timeInMillis

        setAllDayTime()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(
                year,
                month,
                dayOfMonth
            )
        }

        setTimePicker.setOnClickListener {
            if(!isTimePickerDialogOpen){
                isTimePickerDialogOpen = true
                showDialogTimePicker(getTime,getRepeatable)
            }
        }

        cancel.setOnClickListener {
            if(calendarTextView.text.isEmpty()){
                calendar.timeInMillis = System.currentTimeMillis()
                calendarView.date = calendar.timeInMillis
            }
            isDatePickerDialogOpen = false
            dialog.dismiss()
        }

        // Tue, 25-Oct-2023, 5:35 am
        done.setOnClickListener {
            if(getTime.text == "No"){
                setAllDayTime()
                calendarTextView.setText(calculateTime())
                dialog.dismiss()
            }else{
                var time = calculateTime() + ", " + getTime.text
                calendarTextView.setText(time)
                dialog.dismiss()
            }
            isDatePickerDialogOpen = false
        }

        cancelDateImageView.setOnClickListener {
            calendar.timeInMillis = System.currentTimeMillis()
            calendarTextView.setText("")
            cancelDateImageView.visibility = View.GONE
        }


        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun showDialogTimePicker(time:TextView,repeat:TextView){
        val dialog:Dialog = Dialog(this)
        dialog.setContentView(R.layout.time_picker_layout)

        setDialogTimePickerUi(dialog)

        dayViews = arrayOf(sunday, monday, tuesday, wednesday, thursday, friday, saturday)

        setDefaultSelection()
        setListeners()

        switchCompat.setOnClickListener {
            if(noTime.isSelected){
                switchCompat.isChecked = false
                Toast.makeText(this,"Select time",Toast.LENGTH_SHORT).show()
            }else{
                switchCompat.isChecked = !switchCompat.isChecked

                if(switchCompat.isChecked){
                    repeat.text = "No"
                    isRepeated = false

                    switchCompat.isChecked = false
                    hide1.visibility = View.GONE
                }else {
                    repeat.text = "Yes"
                    isRepeated = true

                    switchCompat.isChecked = true
                    hide1.visibility = View.VISIBLE
                }
            }
        }

        buttonDone.setOnClickListener {
            if(noTime.isSelected){
                setAllDayTime()
                time.text = "No"
            }else{
                allDay = false
                time.text = first.text
            }
            isTimePickerDialogOpen = false
            dialog.dismiss()
        }

        buttonCancel.setOnClickListener {
            isTimePickerDialogOpen = false
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
    }


    // defaults selections
    private fun setAllDayTime() {
        hour = 0
        minute  = 0
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        allDay = true
    }
    private fun setDefaultSelection() {
        noTime.isSelected = true
        monday.isSelected = true
        tuesday.isSelected = true
        wednesday.isSelected = true
        thursday.isSelected = true
        friday.isSelected = true
        first.isSelected = false
        second.visibility = View.GONE
        third.visibility = View.GONE
        fourth.visibility = View.GONE
        fifth.visibility = View.GONE
        sixth.visibility = View.GONE

        hide1.visibility = View.GONE
    }
    private fun setListeners() {
        val tempCalendar:Calendar = Calendar.getInstance()
        tempCalendar.timeInMillis = System.currentTimeMillis()

        dayViews.forEach { dayView ->
            dayView.setOnClickListener {
                dayView.isSelected = !dayView.isSelected
            }
        }

        noTime.setOnClickListener {
            if(!noTime.isSelected) {
                noTime.isSelected = !noTime.isSelected
                hide1.visibility = View.GONE
            }

            switchCompat.isChecked = false
            first.isSelected = false
        }

        timePicker.setOnTimeChangedListener { _, hours, minutes ->
            noTime.isSelected = false
            first.isSelected = true

            tempCalendar.set(Calendar.HOUR_OF_DAY, hours)

            calendar.set(Calendar.HOUR_OF_DAY,hours)
            calendar.set(Calendar.MINUTE,minutes)

            hour = hours
            minute = minutes

            var currentHours = tempCalendar.get(Calendar.HOUR)
            var currentMinutes = minutes

            var meridiem: String = "am"

            if (currentHours == 0) {
                currentHours = 12
            }
            if (tempCalendar.get(Calendar.AM_PM) == 1) {
                meridiem = "pm"
            }

            val formattedHours = String.format("%02d", currentHours)
            val formattedMinutes = String.format("%02d", currentMinutes)

            val string = "$formattedHours:$formattedMinutes $meridiem"
            first.text = string
        }

        first.setOnClickListener {
            noTime.isSelected = false
            timePicker.visibility = View.VISIBLE

            var hours = first.text.substring(0,2).toInt()
            var minutes = first.text.substring(3,5).toInt()
            var meridiem = first.text.substring(6,8)

            if(meridiem == "am" && hours == 12){
                hours = 0
            }

            if(meridiem == "pm" && hours != 12){
                hours += (24 - 12)
            }
            timePicker.hour = hours
            timePicker.minute = minutes
        }
    }

    private fun selectedDays(): MutableList<Int> {
        val array = arrayListOf<Int>()

        dayViews.forEachIndexed { index,dayView ->
            if(dayView.isSelected){
                array.add(index+1)
            }else{
                array.add(-1)
            }
        }
        return array
    }


    // for setting alarms & saving in database
    private fun setOnceWithNoDue() {
        var uniqueId = UUID.randomUUID().toString().replace("-", "")
        dataBase.addTask(title.text.toString(),uniqueId)
        dataBase.addDueDatesTasks(uniqueId,
            -1,
            hour,
            minute,
            calendar.get(Calendar.DATE),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR),
            "once"
        )
    }
    private fun setAlarm(title: String) {
        calendar.set(Calendar.SECOND,0)

        var milli = calendar.timeInMillis
        var type = if(allDay) "once_allDay" else "once"
        val dataBase = DataBase(this)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var uniqueId = UUID.randomUUID().toString().replace("-", "")
        var requestId: Int = calendar.timeInMillis.toInt() + Random.nextInt()

        dataBase.addTask(title, uniqueId)
        dataBase.addDueDatesTasks(
            uniqueId,
            requestId,
            hour,
            minute,
            calendar.get(Calendar.DATE),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR),
            type
        )

        // if alarm is set at 12:00 am then add 1 day in the calendar to get exact notification
        if(hour == 0){
            calendar.add(Calendar.DAY_OF_YEAR,1)
            milli = calendar.timeInMillis
        }

        val intent = Intent(this, MyReceiver::class.java)
        intent.action = "SINGLE_ALARM"
        intent.putExtra("ALARM_TITLE", title)
        intent.putExtra("hour", hour)
        intent.putExtra("minute", minute)


        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            requestId,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            milli,
            pendingIntent
        )
    }
    private fun setDailyAlarm(daysList: List<Int>, hours: List<Int>, minutes: List<Int>, title: String) {
        DataSet.setData(daysList, hours, minutes,title)
        var intent:Intent = Intent(this,MyReceiver()::class.java)
        intent.action = "ACTION_INITIALIZATION"
        intent.putExtra("timeInMillis",calendar.timeInMillis)

        sendBroadcast(intent)
    }


    // calculating time if its yet to do or expired
    private fun calculateTime(): String {
        cancelDateImageView.visibility = View.VISIBLE

        val date = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val result = if(isTaskForTodayOrTomorrow(date,month,year)){
            if(date == todayDate) "today" else "tomorrow"
        }else{
            "${days[calendar.get(Calendar.DAY_OF_WEEK)]}, $date-${months[month]}-$year"
        }

        return result
    }
    private fun isTaskForTodayOrTomorrow(date: Int, month: Int, year: Int): Boolean {
        val todayCalendar = Calendar.getInstance()
        todayCalendar.timeInMillis = System.currentTimeMillis()

        var today = todayCalendar.get(Calendar.DATE)
        var todayMonth = todayCalendar.get(Calendar.MONTH)
        var todayYear = todayCalendar.get(Calendar.YEAR)

        // check's if the time is in past
        if (year < todayYear || (year == todayYear && (month < todayMonth || (month == todayMonth && date < today)))) {
            calendarTextView.setTextColor(Color.parseColor("#CF2727"));
            return false
        }

        // check's if current day's time is in past
        if(!allDay && calendar.timeInMillis < todayCalendar.timeInMillis) {
            calendarTextView.setTextColor(Color.parseColor("#CF2727"));
        }else{
            calendarTextView.setTextColor(Color.BLACK);
        }

        // add calendar with +1 date to check if the task's date is tomorrow
        todayCalendar.add(Calendar.DATE,1)

        val tomorrow = todayCalendar.get(Calendar.DATE)
        val tomorrowMonth = todayCalendar.get(Calendar.MONTH)
        val tomorrowYear = todayCalendar.get(Calendar.YEAR)

        return ((today == date && todayMonth == month && todayYear == year) ||
                (tomorrow == date && tomorrowMonth == month && tomorrowYear == year))
    }


    // check if task is entered
    private fun validation(): Boolean {
        if(title.text?.isEmpty() == true){
            Toast.makeText(this,"Enter your task",Toast.LENGTH_SHORT).show()
            title.requestFocus()
            return false
        }
        return true
    }




    // UI
    private fun setDialogTimePickerUi(dialog: Dialog) {
        timePicker = dialog.findViewById(R.id.timePicker)
        switchCompat = dialog.findViewById(R.id.repeatRemainder)
        hide1 = dialog.findViewById(R.id.hide1)
        buttonDone = dialog.findViewById(R.id.done)
        buttonCancel = dialog.findViewById(R.id.cancel)
        sunday = dialog.findViewById(R.id.sunday)
        monday = dialog.findViewById(R.id.monday)
        tuesday = dialog.findViewById(R.id.tuesday)
        wednesday = dialog.findViewById(R.id.wednesday)
        thursday = dialog.findViewById(R.id.thursday)
        friday = dialog.findViewById(R.id.friday)
        saturday = dialog.findViewById(R.id.saturday)
        first = dialog.findViewById(R.id.first)
        second = dialog.findViewById(R.id.second)
        third = dialog.findViewById(R.id.third)
        noTime = dialog.findViewById(R.id.noTime)
        fourth = dialog.findViewById(R.id.fourth)
        fifth = dialog.findViewById(R.id.fifth)
        sixth = dialog.findViewById(R.id.sixth)
    }
    private fun setMainUi() {
        title = findViewById(R.id.addTitle)
        calendarTextView = findViewById(R.id.calendar)
        calendarImageView = findViewById(R.id.calendarImage)
        cancelDateImageView = findViewById(R.id.cancelDate)
        create = findViewById(R.id.createNew)

        sharedPreferences = getSharedPreferences("GesturesFile", MODE_PRIVATE)
    }
}
package com.example.todolist

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.example.todolist.DataBase.DataBase
import java.util.Calendar
import java.util.UUID

class Template : AppCompatActivity() {
    private lateinit var title1:TextView
    private lateinit var title2:TextView
    private lateinit var title3:TextView
    private lateinit var title4:TextView
    private lateinit var title5:TextView
    private lateinit var title6:TextView
    private lateinit var title7:TextView
    private lateinit var title8:TextView
    private lateinit var title9:TextView
    private lateinit var title10:TextView


    private lateinit var item_1:LinearLayout
    private lateinit var item_2:LinearLayout
    private lateinit var item_3:LinearLayout
    private lateinit var item_4:LinearLayout
    private lateinit var item_5:LinearLayout
    private lateinit var item_6:LinearLayout
    private lateinit var item_7:LinearLayout
    private lateinit var item_8:LinearLayout
    private lateinit var item_9:LinearLayout
    private lateinit var item_10:LinearLayout

    private lateinit var sharedPreferences: SharedPreferences


    // for dialog layout
    private lateinit var timePicker:TimePicker
    private lateinit var switchCompat:SwitchCompat
    private lateinit var sunday:TextView
    private lateinit var monday:TextView
    private lateinit var tuesday:TextView
    private lateinit var wednesday:TextView
    private lateinit var thursday:TextView
    private lateinit var friday:TextView
    private lateinit var saturday:TextView
    private lateinit var hide1:LinearLayout
    private lateinit var hide2:RelativeLayout
    private lateinit var button: TextView
    private lateinit var cancel: TextView

    private lateinit var noTime:TextView
    private lateinit var first:TextView
    private lateinit var second:TextView
    private lateinit var third:TextView
    private lateinit var fourth:TextView
    private lateinit var fifth:TextView
    private lateinit var sixth:TextView

    private lateinit var dayViews:Array<TextView>
    private lateinit var timeViews:Array<TextView>
    private lateinit var itemList:Array<LinearLayout>

    private var isDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        // title box for items
        item_1 = findViewById(R.id.addWater)
        item_2 = findViewById(R.id.addExercise)
        item_3 = findViewById(R.id.addReading)
        item_4 = findViewById(R.id.addBed)
        item_5 = findViewById(R.id.addBreak)
        item_6 = findViewById(R.id.addShopping)
        item_7 = findViewById(R.id.addPill)
        item_8 = findViewById(R.id.addGrateFul)
        item_9 = findViewById(R.id.addPlant)
        item_10 = findViewById(R.id.addTasks)


        // title text of items
        title1 = findViewById(R.id.title1)
        title2 = findViewById(R.id.title2)
        title3 = findViewById(R.id.title3)
        title4 = findViewById(R.id.title4)
        title5 = findViewById(R.id.title5)
        title6 = findViewById(R.id.title6)
        title7 = findViewById(R.id.title7)
        title8 = findViewById(R.id.title8)
        title9 = findViewById(R.id.title9)
        title10 = findViewById(R.id.title10)


        val toolbar: Toolbar = findViewById(R.id.template_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences("GesturesFile", MODE_PRIVATE)

        itemList = arrayOf(item_1,item_2,item_3,item_4,item_5,item_6,item_7,item_8,item_9,item_10)

        itemList.forEach { item ->
            item.setOnClickListener {
                if(!isDialogOpen) {
                    isDialogOpen = true
                    showDialogTimePicker(item)
                }
            }
        }
    }

    private fun getTitle(item: LinearLayout) :String{
        return (when(item){
            item_1 -> title1.text.toString()
            item_2 -> title2.text.toString()
            item_3 -> title3.text.toString()
            item_4 -> title4.text.toString()
            item_5 -> title5.text.toString()
            item_6 -> title6.text.toString()
            item_7 -> title7.text.toString()
            item_8 -> title8.text.toString()
            item_9 -> title9.text.toString()
            item_10 -> title10.text.toString()
            else -> title1.text.toString()
        })
    }

    private fun showDialogTimePicker(item:LinearLayout){
        val dialog:Dialog = Dialog(this)

        dialog.setContentView(R.layout.time_picker_layout)

        timePicker = dialog.findViewById(R.id.timePicker)
        switchCompat = dialog.findViewById(R.id.repeatRemainder)
        hide1 = dialog.findViewById(R.id.hide1)
        hide2 = dialog.findViewById(R.id.hide2)
        button = dialog.findViewById(R.id.done)
        cancel = dialog.findViewById(R.id.cancel)
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


        dayViews = arrayOf(sunday, monday, tuesday, wednesday, thursday, friday, saturday)
        timeViews = arrayOf(first,second,third,fourth,fifth,sixth)

        setDefaultSelection()
        setListeners()
        var title = getTitle(item)

        cancel.setOnClickListener {
            isDialogOpen = false
            dialog.dismiss()
        }

        button.setOnClickListener {
            if(!monday.isSelected && !tuesday.isSelected && !wednesday.isSelected && !thursday.isSelected && !friday.isSelected && !saturday.isSelected && !sunday.isSelected){
                Toast.makeText(this,"select day",Toast.LENGTH_SHORT).show()
            }else {
                AllTasks.newDataAdded(true)
                val daysList = selectedDays()
                val timeList = selectedTime()
                val formattedHours = formatHour(timeList)
                val formattedMinutes = formatMinutes(timeList)

                setDailyAlarm(daysList, formattedHours, formattedMinutes, title)
                dialog.dismiss()
                onBackPressed()
            }
        }

        dialog.show()
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        isDialogOpen = false
    }

    private fun setSingleAlarm(title: String,hours:MutableList<Int>,minutes: MutableList<Int>) {
        val dataBase = DataBase(this)

        for(i in 0..< hours.size) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK))
            calendar.set(Calendar.HOUR_OF_DAY, hours[i])
            calendar.set(Calendar.MINUTE, minutes[i])
            calendar.set(Calendar.SECOND, -5)

            var uniqueId = UUID.randomUUID().toString().replace("-","")
            var requestId: Int = calendar.timeInMillis.toInt() + kotlin.random.Random.nextInt()

            if (calendar.timeInMillis < System.currentTimeMillis()) {
                Toast.makeText(this, "Alarm time expired", Toast.LENGTH_SHORT).show()
                return
            }
            dataBase.addTask(title,uniqueId)

            dataBase.addDueDatesTasks(
                uniqueId,
                requestId,
                hours[i],
                minutes[i],
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR),
                "once")

            val intent = Intent(this, MyReceiver::class.java)
            intent.action = "SINGLE_ALARM"
            intent.putExtra("ALARM_TITLE", title)
            intent.putExtra("hour",hours[i])
            intent.putExtra("minute",minutes[i])


            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                requestId,
                intent,
                PendingIntent.FLAG_MUTABLE
            )

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        onBackPressed()
    }

    private fun setDailyAlarm(
        daysList: MutableList<Int>,
        hours: MutableList<Int>,
        minutes: MutableList<Int>,
        title: String
    )
    {
        DataSet.setData(daysList,hours,minutes,title)
        var intent:Intent = Intent(this,MyReceiver()::class.java)
        intent.action = "ACTION_INITIALIZATION"
        intent.putExtra("timeInMillis",System.currentTimeMillis())

        sendBroadcast(intent)
    }
    private fun setListeners() {
        val calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        dayViews.forEach { dayView ->
            dayView.setOnClickListener {
                dayView.isSelected = !dayView.isSelected
            }
        }


        var inital:TextView? = first
        timePicker.setOnTimeChangedListener { _, hours, minutes ->

            if(inital != null && !noTime.isSelected){
                calendar.set(Calendar.HOUR_OF_DAY,hours)
                calendar.set(Calendar.MINUTE,minutes)

                var currentHours = calendar.get(Calendar.HOUR)
                var currentMinutes = minutes
                var meridiem:String = "am"

                if(currentHours == 0){
                    currentHours = 12
                }
                if(calendar.get(Calendar.AM_PM) == 1){
                    meridiem = "pm"
                }

                val formattedHours = String.format("%02d",currentHours)
                val formattedMinutes = String.format("%02d",currentMinutes)

                val string:String = "$formattedHours:$formattedMinutes $meridiem"
                inital?.text = string
            }
        }

        timeViews.forEach { timeView ->
            timeView.setOnClickListener {
                inital = timeView
                timeView.isSelected = !timeView.isSelected
                timePicker.visibility = View.VISIBLE


                if(timeView.isSelected){
                    var hours = timeView.text.substring(0,2).toInt()
                    var minutes = timeView.text.substring(3,5).toInt()
                    var meridiem = timeView.text.substring(6,8)

                    if(meridiem == "am" && hours == 12){
                        hours = 0
                    }

                    if(meridiem == "pm" && hours != 12){
                        hours += (24 - 12)
                    }
                    timePicker.hour = hours
                    timePicker.minute = minutes

                    Log.e("TAG", "${timePicker.hour}:${timePicker.minute} ", )
                }else{
                    inital = null
                }
            }
        }
    }
    private fun formatMinutes(timeList: MutableList<TextView>):MutableList<Int> {
        val array = arrayListOf<Int>()
        timeList.forEach { time ->
            if(time.isSelected){
                array.add(time.text.substring(3,5).toInt())
            }
        }
        return array
    }
    private fun formatHour(timeList: MutableList<TextView>):MutableList<Int>{
        val array = arrayListOf<Int>()

        timeList.forEach {time ->
            if(time.isSelected){
                var hours = time.text.substring(0,2).toInt()
                var meridiem = time.text.substring(6,8)


                if(meridiem == "am" && hours == 12){
                    hours = 24
                }

                if(meridiem == "pm" && hours != 12){
                    hours += (24 - 12)
                }

                array.add(hours)
            }
        }
        return array
    }

    private fun setDefaultSelection() {
        hide1.visibility = View.VISIBLE
        switchCompat.visibility = View.GONE
        noTime.visibility = View.GONE

        val  calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        when(calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> sunday.isSelected = true
            2 -> monday.isSelected = true
            3 -> tuesday.isSelected = true
            4 -> wednesday.isSelected = true
            5 -> thursday.isSelected = true
            6 -> friday.isSelected = true
            7 -> saturday.isSelected = true
        }

        first.isSelected = true
        second.isSelected = false
        third.isSelected = false
        fourth.isSelected = false
        fifth.isSelected = false
        sixth.isSelected = false
    }
    private fun selectedDays(): MutableList<Int> {
        val array = arrayListOf<Int>()

        dayViews.forEachIndexed { index,dayView ->
            if(dayView.isSelected){
                array.add(index+1)
            }
        }
        return array
    }
    private fun selectedTime(): MutableList<TextView> {
        val array = arrayListOf<TextView>()

        timeViews.forEach { timeView ->
            if(timeView.isSelected){
                array.add(timeView)
            }
        }
        return array
    }
    private fun visible() {
        timePicker.visibility = View.VISIBLE
        hide1.visibility = View.VISIBLE
        hide2.visibility = View.VISIBLE
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
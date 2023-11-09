package com.example.todolist

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todolist.DataBase.DataBase
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random


class MyReceiver:BroadcastReceiver() {
    companion object {
        private var CHANNEL_ID_1: String = "1001"
        private var CHANNEL_ID_2: String = "1002"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action == "ACTION_INITIALIZATION"){
            var timeInMillis = intent.getLongExtra("timeInMillis",0)
            setInitialAlarm(timeInMillis,context, DataSet.getDays(), DataSet.getHours(),DataSet.getMinutes(),DataSet.getTitle())
            intent.removeExtra("timeInMillis");
        }
//        else if(intent?.action == "DAILY_REMINDER_ACTION") {
//            Log.e("TAG", "daily_reminder", )
//            implementDailyReminder(context, intent)
//        }
        else if(intent?.action == "DAILY_ALARM_ACTION"){
            implementAlarm(context,intent)

            var hour: Int? = intent.getIntExtra("hour", 0)
            var minute: Int? = intent.getIntExtra("minute", 0)
            var day: Int? = intent.getIntExtra("day", 0)
            var title:String? = intent.getStringExtra("ALARM_TITLE")
            var requestId:Int = intent.getIntExtra("requestId", 0)
            var uniqueId:String? = intent.getStringExtra("uniqueId")
            var timeInMillis = intent.getLongExtra("timeInMillis",0)

            if (hour != null && minute != null && day != null && title != null) {
                setDailyAlarm(timeInMillis,context, hour, minute, day,title,uniqueId,requestId,"none")
            }
        }else if(intent?.action == "SINGLE_ALARM"){
            implementAlarm(context,intent)
        }
    }

    private fun implementAlarm(context: Context, intent: Intent?) {
        val notificationManager: NotificationManager =
            context.getSystemService(NotificationManager::class.java)

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID_2)
            .setContentTitle(intent?.getStringExtra("ALARM_TITLE"))
            .setContentText(getTime(intent))
            .setSmallIcon(R.mipmap.app_image)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(    // used because, when user click's to the notification than he is directed to app's activity
                    context,
                    0,
                    Intent(context, TabLayoutActivity::class.java),
                    PendingIntent.FLAG_MUTABLE
                )
            )
            .build()

        notificationManager.notify(intent.hashCode(), notification)
    }

//    private fun setDailyAlarm(
//        timeInMillis: Long,
//        context: Context?,
//        hour:Int,
//        minute:Int,
//        currentDay:Int,
//        title:String,
//        uniqueId:String?,
//        requestId:Int,
//        type:String
//    ) {
//        var dataBase:DataBase = DataBase(context)
//
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        var calendar: Calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//
//        calendar.set(Calendar.HOUR_OF_DAY, hour)
//        calendar.set(Calendar.DAY_OF_WEEK, currentDay)
//        calendar.set(Calendar.MINUTE, minute)
//        calendar.set(Calendar.SECOND, -3)
//
//        if (calendar.timeInMillis - 5000 < System.currentTimeMillis()) {
//            Log.e("NEXT WEEK TAG", "$hour: $minute --> ${calendar.get(Calendar.DATE)}", )
//            calendar.add(Calendar.DAY_OF_YEAR, 7)
//            if(type != "none"){
//                dataBase.addDueDatesTasks(
//                    uniqueId,
//                    requestId,
//                    hour,
//                    minute,
//                    calendar.get(Calendar.DATE),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.YEAR),
//                    type
//                )
//            }
//        }else {
//            Log.e("CURRENT WEEK TAG", "$hour: $minute --> ${calendar.get(Calendar.DATE)}",)
//            dataBase.addDueDatesTasks(
//                uniqueId,
//                requestId,
//                hour,
//                minute,
//                calendar.get(Calendar.DATE),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.YEAR),
//                type
//            )
//        }
//
//        val intent = Intent(context, MyReceiver::class.java)
//
//        intent.action = "DAILY_ALARM_ACTION"
//        intent.putExtra("ALARM_TITLE", title)
//        intent.putExtra("hour", hour)
//        intent.putExtra("minute", minute)
//        intent.putExtra("uniqueId",uniqueId)
//        intent.putExtra("requestId",requestId)
//        intent.putExtra("day", currentDay)
//        intent.putExtra("timeInMillis",timeInMillis)
//
//        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
//            context,
//            requestId,
//            intent,
//            PendingIntent.FLAG_MUTABLE
//        )
//
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent,
//        )
//    }

    private fun setDailyAlarm(
        timeInMillis: Long,
        context: Context?,
        hour: Int,
        minute: Int,
        currentDay: Int,
        title: String,
        uniqueId: String?,
        requestId: Int,
        type: String
    ) {
        val system = Calendar.getInstance()
        system.timeInMillis = timeInMillis
        system.set(Calendar.HOUR_OF_DAY,0)
        system.set(Calendar.MINUTE,0)
        system.set(Calendar.SECOND,0)

        val dataBase: DataBase = DataBase(context)


        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis


        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK,currentDay)

        val date = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        var today = system.get(Calendar.DATE)
        var todayMonth = system.get(Calendar.MONTH)
        var todayYear = system.get(Calendar.YEAR)

        if (calendar.timeInMillis - 5000 < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            Log.e("Past - Future WEEK TAG", "$hour: $minute --> ${calendar.get(Calendar.DATE)}")
            if (type != "none") {
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
            }
        } else {
            if ((year < todayYear || (year == todayYear && (month < todayMonth || (month == todayMonth && date < today))))) {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            }
            Log.e("Future TAG", "$hour: $minute --> ${calendar.get(Calendar.DATE)}")
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
        }

        val intent = Intent(context, MyReceiver::class.java)

        intent.action = "DAILY_ALARM_ACTION"
        intent.putExtra("ALARM_TITLE", title)
        intent.putExtra("hour", hour)
        intent.putExtra("minute", minute)
        intent.putExtra("uniqueId", uniqueId)
        intent.putExtra("requestId", requestId)
        intent.putExtra("day", currentDay)
        intent.putExtra("timeInMillis", calendar.timeInMillis)

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            requestId,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent,
        )
    }


    private fun setInitialAlarm(
        timeInMillis: Long,
        context: Context,
        daysList:List<Int>?,
        hours:List<Int>?,
        minutes:List<Int>?,
        title:String
    ) {
        var currentDay: Int
        var dataBase: DataBase = DataBase(context)
        var type = if (daysList?.size == 7) "daily" else "custom"


        var calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        for(i in 0..< hours!!.size) {
            var hour = hours?.get(i)
            var minute = minutes?.get(i)


            if (hour != null && minute != null) {
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
            }

            var uniqueId = UUID.randomUUID().toString().replace("-", "")

            dataBase.addTask(title, uniqueId)

            for (j in daysList!!.indices) {
                currentDay = daysList[j]

                var requestId: Int = calendar.timeInMillis.toInt() + Random.nextInt()
                if (currentDay != -1 && hour != null && minute != null) {
                    setDailyAlarm(
                        calendar.timeInMillis,
                        context,
                        hour,
                        minute,
                        currentDay,
                        title,
                        uniqueId,
                        requestId,
                        type
                    )
                }
            }
        }
    }






















//    private fun setInitialAlarm(
//        timeInMillis: Long,
//        context: Context,
//        daysList:List<Int>?,
//        hours:List<Int>?,
//        minutes:List<Int>?,
//        title:String
//    ) {
//        var currentDay:Int
//        var dataBase:DataBase = DataBase(context)
//        var type = if(daysList?.size == 7) "daily" else "custom"
//
//        for(i in 0..< hours!!.size) {
//            var calendar:Calendar = Calendar.getInstance()
//            calendar.timeInMillis = System.currentTimeMillis()
//            var hour = hours?.get(i)
//            var minute = minutes?.get(i)
//
//
//            if(hour != null && minute != null) {
//                calendar.set(Calendar.HOUR_OF_DAY, hour)
//                calendar.set(Calendar.MINUTE, minute)
//            }
//
//            var uniqueId = UUID.randomUUID().toString().replace("-","")
//
//            dataBase.addTask(title,uniqueId)
//
//            for (j in daysList!!.indices) {
//                currentDay = daysList[j]
//
//                var requestId:Int = calendar.timeInMillis.toInt() + Random.nextInt()
//                if (hour != null && minute != null) {
//                    setDailyAlarm(calendar.timeInMillis,context, hour, minute,currentDay,title,uniqueId,requestId,type)
//                }
//            }
//        }
//    }

    private fun implementDailyReminder(context: Context?,intent: Intent?) {
        val notificationManager:NotificationManager = context!!.getSystemService(NotificationManager::class.java)

        // firstly we did startForeground() but since Android 8.0 (API 26) we must set Notification Channel
        val notification: Notification = NotificationCompat.Builder(context,CHANNEL_ID_1)
            .setContentTitle("Time to schedule tasks")
            .setContentText("Enjoy your day!")
            .setSmallIcon(R.mipmap.app_image)
            .setContentIntent(
                PendingIntent.getActivity(    // used because, when user click's to the notification than he is directed to app's activity
                context,
                intent.hashCode(),
                Intent(context,TabLayoutActivity::class.java),
                PendingIntent.FLAG_MUTABLE)
            )
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(intent.hashCode(),notification)
    }

    private fun getTime(intent: Intent?):String {
        val calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        var hour:Int
        if(intent != null) {
            calendar.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("hour",0))
            calendar.set(Calendar.MINUTE, intent.getIntExtra("minute",0))
        }
        if(intent?.getIntExtra("hour",0) == 12 || intent?.getIntExtra("hour",0) == 24){
            hour = 12
        }else{
            hour = calendar.get(Calendar.HOUR)
        }

        return  ("${hour}:${String.format("%02d",calendar.get(Calendar.MINUTE))} ${if (calendar.get(Calendar.AM_PM) == 0) "am" else "pm"}")
    }
}
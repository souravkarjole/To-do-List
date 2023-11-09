package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout


class TabLayoutActivity : AppCompatActivity() {
    private var CHANNEL_ID_1: String = "1001"
    private var CHANNEL_ID_2: String = "1002"
    lateinit var menu: Menu
    lateinit var tabLayout:TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout_activity)
        createNotificationChannel()

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewpager)

        checkPermission()
        setTabLayout()

        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun createNotificationChannel() {
        val notificationChannel1: NotificationChannel = NotificationChannel(
            CHANNEL_ID_1,
            "ScheduleTasks",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel1.description = "ScheduleTasks"


        val notificationChannel2: NotificationChannel = NotificationChannel(
            CHANNEL_ID_2,
            "StickyList",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel2.description = "To-Do-Alarm"

        val notificationManager: NotificationManager? = getSystemService(
            NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel1)
        notificationManager?.createNotificationChannel(notificationChannel2)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 10){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setAlarmManager()
            }else{
                Toast.makeText(this,"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(){

        // minSDK must be at-least 33 to ask for permission from user
        if(Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10
            )
        }else{
            setAlarmManager()
        }
    }

    private fun setAlarmManager(){
//        val alarmManager:AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val intent:Intent = Intent(this,MyReceiver::class.java)
//        intent.action = "DAILY_REMINDER_ACTION"
//
//        val pendingIntent:PendingIntent =
//            PendingIntent.getBroadcast(
//                this,
//                intent.hashCode(),
//                intent,
//                PendingIntent.FLAG_MUTABLE
//            )
//
//        val timeAtMillis:Long = System.currentTimeMillis()
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,timeAtMillis,AlarmManager.INTERVAL_DAY,pendingIntent)
    }

    private fun setTabLayout() {
        val viewPagerAdapter:ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager,lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("To-do"))
        tabLayout.addTab(tabLayout.newTab().setText("Completed"))
        tabLayout.addTab(tabLayout.newTab().setText("Incomplete"))
        tabLayout.addTab(tabLayout.newTab().setText("Overview"))

        viewPager.adapter = viewPagerAdapter
        

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager.registerOnPageChangeCallback(object :OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

}
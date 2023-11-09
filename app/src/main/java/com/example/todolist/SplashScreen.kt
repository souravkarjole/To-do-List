package com.example.todolist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

class SplashScreen : AppCompatActivity() {

    private var animationFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val sharedPreferences: SharedPreferences = getSharedPreferences("OnBoardingScreenFile", MODE_PRIVATE)

        // default value is true, i.e user first time installed and hence FLAG = true
        var firstLaunch = sharedPreferences.getBoolean("checkFlag", true)

        // if its first time launch than below "if statement" will not execute, because user just now installed the app.
        // if user already visited current activity than below "if statement" will execute and goes to next activity.
        val stickyList: TextView = findViewById(R.id.stickyList)

        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.scale)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                animationFinished = true
                startNextActivityIfNeeded(firstLaunch)
            }

        })

        stickyList.startAnimation(animation)
    }

    private fun startNextActivityIfNeeded(firstLaunch: Boolean) {
        if (animationFinished) {
            if (!firstLaunch) {
                startActivity(Intent(applicationContext, TabLayoutActivity::class.java))
            } else {
                startActivity(Intent(applicationContext, OnBoardingScreen::class.java))
            }
            finish()
        }
    }
}
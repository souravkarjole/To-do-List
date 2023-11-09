package com.example.todolist

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class OnBoardingScreen : AppCompatActivity() {
    lateinit var button:Button
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding_screen)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        sharedPreferences = getSharedPreferences("OnBoardingScreenFile",
            MODE_PRIVATE)

        button = findViewById(R.id.continue_)


        button.setOnClickListener {
            // now we will set the FLAG = false, because user already visited first time
            showTutorialBottomSheet()
        }
    }

    private fun showTutorialBottomSheet() {
        val dialog: Dialog = Dialog(this)
        val view: View = layoutInflater.inflate(R.layout.tutorial,null)

        dialog.setContentView(view)

        val okay:TextView = dialog.findViewById(R.id.ok)

        okay.setOnClickListener {
            var editor:Editor = sharedPreferences.edit()
            editor.putBoolean("checkFlag",false)
            editor.apply()

            startActivity(Intent(this,TabLayoutActivity::class.java))
            dialog.dismiss()
            finish()
        }

        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
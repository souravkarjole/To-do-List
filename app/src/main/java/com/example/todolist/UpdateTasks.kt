package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.todolist.DataBase.DataBase
import com.google.android.material.textfield.TextInputEditText

class UpdateTasks : AppCompatActivity() {

    private lateinit var textView:TextView
    private lateinit var update:Button
    private lateinit var title:TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)

        title = findViewById(R.id.addTitle)
        textView = findViewById(R.id.updateText)
        update = findViewById(R.id.createNew)

        setPreviousNote()

        update.setOnClickListener {

            if(validation()) {
                AllTasks.newDataAdded(true)
                val dataBase: DataBase =
                    DataBase(this)

                dataBase.updateNote(
                    intent.getStringExtra("id").toString(),
                    title.text.toString()
                )
                onBackPressed()
            }
        }
    }

    private fun validation(): Boolean {
        if(title.text?.isEmpty() == true){
            title.error = "title is missing"
            title.requestFocus()
            return false
        }
        return true
    }


    private fun setPreviousNote() {
        textView.text = "Updating Task!"
        update.text = "Update"

        title.setText(intent.getStringExtra("title"))
    }
}
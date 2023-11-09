package com.example.todolist


class DataSet {
    companion object {
        private var days: List<Int>? = null
        private var hours: List<Int>? = null
        private var minutes: List<Int>? = null
        private var title: String = "TITLE"

        fun setData(
            newDays: List<Int>,
            newHours: List<Int>,
            newMinutes: List<Int>,
            newTitle: String
        ) {
            days = newDays
            hours = newHours
            minutes = newMinutes
            title = newTitle
        }

        fun getDays(): List<Int>? {
            return days
        }

        fun getHours(): List<Int>? {
            return hours
        }

        fun getMinutes(): List<Int>? {
            return minutes
        }

        fun getTitle():String{
            return title
        }
    }
}
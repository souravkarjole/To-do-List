package com.example.todolist

class TasksItemView {
    var id: String = ""
    var title: String = ""
    var month: Int = -1
    var year: Int = -1
    var date: Int = -1
    var hour: Int = -1
    var minute: Int = -1
    var requestId:Int = -1
    var uniqueId:String = ""
    var type:String = ""

    constructor(
        id:String,
        title:String,
        uniqueId: String)
    {
        this.id = id
        this.title = title
        this.uniqueId = uniqueId
    }
    constructor(
        id:String,
        title:String,
        requestId: Int,
        uniqueId:String,
        date:Int,
        month: Int,
        year: Int,
        hour:Int,
        minute:Int,
        type: String)
    {
        this.id = id
        this.title = title
        this.requestId = requestId
        this.uniqueId = uniqueId
        this.date = date
        this.month = month
        this.year = year
        this.hour = hour
        this.minute = minute
        this.type = type
    }


}
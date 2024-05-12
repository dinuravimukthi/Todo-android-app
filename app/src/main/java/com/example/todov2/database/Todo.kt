package com.example.todov2.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    var item:String,
    var description:String?,
    var priority: Int?,
    var deadline: String?

){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}

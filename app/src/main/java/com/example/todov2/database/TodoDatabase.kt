package com.example.todov2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 4)
abstract class TodoDatabase:RoomDatabase() {
    abstract fun getTodoDao():TodoDao

    companion object{
        @Volatile
        private var INSTANCE:TodoDatabase? = null

        fun getInstance(context:Context):TodoDatabase{
            synchronized(this){
                return INSTANCE?: Room.databaseBuilder(
                    context,
                    TodoDatabase::class.java,
                    "todo_db"
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
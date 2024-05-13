package com.example.todov2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {

    @Insert
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("SELECT * FROM Todo")
    fun getAllTodoItems(): List<Todo>

    @Query("SELECT * FROM Todo WHERE id=:id")
    fun getOne(id:Int):Todo
}
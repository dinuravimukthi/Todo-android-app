package com.example.todov2.database

class TodoRepository(
    private val db:TodoDatabase
){
    suspend fun insert(todo: Todo) = db.getTodoDao().insert(todo)
    suspend fun delete(todo: Todo) = db.getTodoDao().delete(todo)
    suspend fun update(todo: Todo) = db.getTodoDao().update(todo)

    fun getAllTodoItems():List<Todo> = db.getTodoDao().getAllTodoItems()
}
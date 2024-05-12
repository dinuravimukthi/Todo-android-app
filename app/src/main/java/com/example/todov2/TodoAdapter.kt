package com.example.todov2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todov2.database.Todo
import com.example.todov2.database.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoAdapter(items:List<Todo>, repository:TodoRepository,
    viewModel:MainActivityData):Adapter<TodoViewHolder>() {

    var context:Context? = null
    val items = items
    val repository = repository
    val viewModel = viewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        context = parent.context

        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val text = ("Title: " + items.get(position).item + "\n"
                + "Description: "+ items.get(position).description + "\n"
                + "Priority: " + items.get(position).priority + "\n"
                + "Deadline: " + items.get(position).deadline).toString()
        holder.cbTodo.text = text
        holder.ivDelete?.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked

            if (isChecked){
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(items.get(position))
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main){
                        viewModel.setData(data)
                    }
                }
            } else {
                Toast.makeText(context, "Select the item to be deleted", Toast.LENGTH_LONG).show()
            }
        }
    }
}
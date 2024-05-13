package com.example.todov2

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todov2.database.Todo
import com.example.todov2.database.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

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

    fun deleteItem(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.delete(items.get(position))
            val data = repository.getAllTodoItems()
            withContext(Dispatchers.Main){
                viewModel.setData(data)
            }
        }
    }



    fun editItem(context: Context,position: Int) {
        val todo = items[position]

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Task")

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        // Create EditText for title
        val titleEditText = EditText(context)
        titleEditText.inputType = InputType.TYPE_CLASS_TEXT
        titleEditText.setText(todo.item)
        layout.addView(titleEditText)

        // Create EditText for description
        val descriptionEditText = EditText(context)
        descriptionEditText.setText(todo.description)
        layout.addView(descriptionEditText)

        // Create EditText for priority
        val priorityEditText = EditText(context)
        priorityEditText.inputType = InputType.TYPE_CLASS_NUMBER
        priorityEditText.setText(todo.priority.toString())
        layout.addView(priorityEditText)

        // Create TextView and Button for deadline
        val deadlineTextView = TextView(context)
        deadlineTextView.text = "Deadline: ${todo.deadline}"
        layout.addView(deadlineTextView)

        val deadlineButton = Button(context)
        deadlineButton.text = "Select Deadline"
        layout.addView(deadlineButton)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        deadlineButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                deadlineTextView.text = selectedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        builder.setView(layout)

        builder.setPositiveButton("Update") { dialog, which ->
            val newTitle = titleEditText.text.toString()
            val newDescription = descriptionEditText.text.toString()
            val newPriority = priorityEditText.text.toString().toIntOrNull() ?: 0
            val newDeadline = deadlineTextView.text.toString()

            // Update the task
            CoroutineScope(Dispatchers.IO).launch {
                //todo.id,
                val updatedTodo = Todo(newTitle, newDescription, newPriority, newDeadline)
                updatedTodo.id = todo.id
                repository.update(updatedTodo)

                val data = repository.getAllTodoItems()
                withContext(Dispatchers.Main){
                    viewModel.setData(data)
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

}
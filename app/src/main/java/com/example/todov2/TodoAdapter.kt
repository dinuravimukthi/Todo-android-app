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

        holder.ivDelete.setOnClickListener{
            val builder = this.context?.let { AlertDialog.Builder(it) }
            builder?.setTitle("Delete Task")
            builder?.setMessage("Are you sure you want to delete this Task?")
            builder?.setPositiveButton(
                "Confirm"
            ) { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(items.get(position))
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main){
                        viewModel.setData(data)
                    }
                }
                Toast.makeText(context,"Item Deleted",Toast.LENGTH_LONG).show()
            }
           builder?.setNegativeButton(
                R.string.cancel
           ){dialog, which -> this.notifyItemChanged(items.size)}
            val dialog = builder?.create()
            dialog?.show()


        }

        holder.ivEdit.setOnClickListener{
            val todo = items[position]

            val builder = this.context?.let { it1 -> AlertDialog.Builder(it1) }
            builder?.setTitle("Edit Task")

            val layout = LinearLayout(this.context)
            layout.orientation = LinearLayout.VERTICAL

            // Create EditText for title
            val titleEditText = EditText(this.context)
            titleEditText.inputType = InputType.TYPE_CLASS_TEXT
            titleEditText.setText(todo.item)
            layout.addView(titleEditText)

            // Create EditText for description
            val descriptionEditText = EditText(this.context)
            descriptionEditText.setText(todo.description)
            layout.addView(descriptionEditText)

            // Create EditText for priority
            val priorityEditText = EditText(this.context)
            priorityEditText.inputType = InputType.TYPE_CLASS_NUMBER
            priorityEditText.setText(todo.priority.toString())
            layout.addView(priorityEditText)

            // Create TextView and Button for deadline
            val deadlineTextView = TextView(this.context)
            deadlineTextView.text = todo.deadline.toString()
            layout.addView(deadlineTextView)

            val deadlineButton = Button(this.context)
            deadlineButton.text = (this.context?.getString(R.string.select_deadline) ?: "")
            layout.addView(deadlineButton)

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            deadlineButton.setOnClickListener {
                val datePickerDialog =
                    this.context?.let { it1 ->
                        DatePickerDialog(it1, { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                            deadlineTextView.text = selectedDate
                        }, year, month, day)
                    }
                datePickerDialog?.show()
            }

            builder?.setView(layout)

            builder?.setPositiveButton("Update") { dialog, which ->
                val newTitle = titleEditText.text.toString()
                val newDescription = descriptionEditText.text.toString()
                val newPriority = priorityEditText.text.toString().toIntOrNull() ?: 0
                val newDeadline = deadlineTextView.text.toString()

                // Update the task
                CoroutineScope(Dispatchers.IO).launch {
                    val updatedTodo = Todo(newTitle, newDescription, newPriority, newDeadline)
                    updatedTodo.id = todo.id
                    repository.update(updatedTodo)

                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main){
                        viewModel.setData(data)
                    }
                }
            }

            builder?.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            builder?.show()
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
        deadlineTextView.text = todo.deadline.toString()
        layout.addView(deadlineTextView)

        val deadlineButton = Button(context)
        deadlineButton.text = context.getString(R.string.select_deadline)
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
package com.example.todov2

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todov2.database.Todo
import com.example.todov2.database.TodoDatabase
import com.example.todov2.database.TodoRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: TodoAdapter
    private lateinit var viewModel:MainActivityData
    private var fab: FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.tasksRecyclerView)
        val repository = TodoRepository(TodoDatabase.getInstance(this))

        viewModel = ViewModelProvider(this)[MainActivityData::class.java]

        viewModel.data.observe(this){
            adapter = TodoAdapter(it,repository,viewModel)
            // set adapter
            recyclerView!!.adapter = adapter
            // set the layout manager
            recyclerView!!.layoutManager = LinearLayoutManager(this)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()

            runOnUiThread{
                viewModel.setData(data)
            }
        }

        fab = findViewById<FloatingActionButton>(R.id.fab)

        fab!!.setOnClickListener(View.OnClickListener {
            displayAlert(repository)
        })

    }

    private fun displayAlert(repository:TodoRepository){
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getText(R.string.alert_title))
        builder.setMessage(R.string.alert_message)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Create EditText for title
        val titleEditText = EditText(this)
        titleEditText.inputType = InputType.TYPE_CLASS_TEXT
        titleEditText.hint = "Title"
        layout.addView(titleEditText)

        // Create EditText for description
        val descriptionEditText = EditText(this)
        descriptionEditText.hint = "Description"
        layout.addView(descriptionEditText)

        // Create EditText for priority
        val priorityEditText = EditText(this)
        priorityEditText.hint = "Priority"
        priorityEditText.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(priorityEditText)

        // Create EditText for deadline
//        val deadlineEditText = EditText(this)
//        deadlineEditText.hint = "Deadline"
//        deadlineEditText.inputType = InputType.TYPE_CLASS_DATETIME
//        layout.addView(deadlineEditText)
        // Create TextView and Button for deadline
        val deadlineTextView = TextView(this)
        deadlineTextView.hint = "Deadline"
        layout.addView(deadlineTextView)

        val deadlineButton = Button(this)
        deadlineButton.text = "Select Deadline"
        layout.addView(deadlineButton)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        deadlineButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                deadlineTextView.text = selectedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        builder.setView(layout)

        builder.setPositiveButton("Ok"){dialog,which ->
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val priority = priorityEditText.text.toString().toIntOrNull() ?: 0
            val deadline = deadlineTextView.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(Todo(title,description,priority,deadline))
                val data = repository.getAllTodoItems()
                runOnUiThread{
                    viewModel.setData(data)
                }
            }
        }

        builder.setNegativeButton("Cancel"){dialog,which ->
            dialog.cancel()
        }

        // Create and show the alert dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

}
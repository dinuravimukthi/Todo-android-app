package com.example.todov2

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TodoViewHolder(view: View):ViewHolder(view) {
    val cbTodo:CheckBox = view.findViewById(R.id.todoCheckBox)
    val ivDelete:Button? = null
}
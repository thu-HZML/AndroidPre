package com.example.firstcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class TodoListState(
    initialTasks: List<Task> = emptyList()
) {
    var tasks by mutableStateOf(initialTasks)
        private set

    fun addTask(rawTitle: String): Boolean {
        val title = rawTitle.trim()
        if (title.isBlank()) return false

        val nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
        tasks = tasks + Task(
            id = nextId,
            title = title,
            isCompleted = false
        )
        return true
    }

    fun toggleTask(id: Int, checked: Boolean) {
        tasks = tasks.map { task ->
            if (task.id == id) task.copy(isCompleted = checked) else task
        }
    }

    fun deleteTask(id: Int) {
        tasks = tasks.filter { task -> task.id != id }
    }
}

@Composable
fun rememberTodoListState(
    initialTasks: List<Task> = emptyList()
): TodoListState = remember {
    TodoListState(initialTasks = initialTasks)
}

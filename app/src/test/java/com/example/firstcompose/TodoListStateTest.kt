package com.example.firstcompose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoListStateTest {

    @Test
    fun addTask_addsTrimmedTask() {
        val state = TodoListState()

        val added = state.addTask("  Finish slides  ")

        assertTrue(added)
        assertEquals(1, state.tasks.size)
        assertEquals("Finish slides", state.tasks.first().title)
        assertFalse(state.tasks.first().isCompleted)
    }

    @Test
    fun addTask_rejectsBlankInput() {
        val state = TodoListState()

        val added = state.addTask("   ")

        assertFalse(added)
        assertTrue(state.tasks.isEmpty())
    }

    @Test
    fun toggleTask_updatesCompletionState() {
        val state = TodoListState(
            initialTasks = listOf(Task(id = 1, title = "Task", isCompleted = false))
        )

        state.toggleTask(id = 1, checked = true)

        assertTrue(state.tasks.first().isCompleted)
    }

    @Test
    fun deleteTask_removesMatchingTask() {
        val state = TodoListState(
            initialTasks = listOf(
                Task(id = 1, title = "Task 1", isCompleted = false),
                Task(id = 2, title = "Task 2", isCompleted = true)
            )
        )

        state.deleteTask(id = 1)

        assertEquals(1, state.tasks.size)
        assertEquals(2, state.tasks.first().id)
    }
}

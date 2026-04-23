package com.example.taskcatalog.mapper

import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.model.Task
import org.springframework.stereotype.Component

@Component
class TaskMapper {
    fun toResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id ?: throw IllegalStateException("Task ID must not be null when mapping to response"),
            title = task.title,
            description = task.description,
            status = task.status,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}

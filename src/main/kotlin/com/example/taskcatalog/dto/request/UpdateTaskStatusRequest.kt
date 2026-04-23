package com.example.taskcatalog.dto.request

import com.example.taskcatalog.model.TaskStatus
import jakarta.validation.constraints.NotNull

data class UpdateTaskStatusRequest(
    @field:NotNull(message = "Status must not be null")
    val status: TaskStatus
)

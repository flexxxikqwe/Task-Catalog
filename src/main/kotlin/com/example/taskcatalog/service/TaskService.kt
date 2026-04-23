package com.example.taskcatalog.service

import com.example.taskcatalog.dto.request.CreateTaskRequest
import com.example.taskcatalog.dto.request.UpdateTaskStatusRequest
import com.example.taskcatalog.dto.response.PageResponse
import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.model.TaskStatus
import reactor.core.publisher.Mono

interface TaskService {
    fun createTask(request: CreateTaskRequest): Mono<TaskResponse>
    fun getTaskById(id: Long): Mono<TaskResponse>
    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>>
    fun updateTaskStatus(id: Long, request: UpdateTaskStatusRequest): Mono<TaskResponse>
    fun deleteTask(id: Long): Mono<Void>
}

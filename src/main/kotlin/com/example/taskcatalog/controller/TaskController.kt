package com.example.taskcatalog.controller

import com.example.taskcatalog.dto.request.CreateTaskRequest
import com.example.taskcatalog.dto.request.UpdateTaskStatusRequest
import com.example.taskcatalog.dto.response.PageResponse
import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.model.TaskStatus
import com.example.taskcatalog.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): Mono<TaskResponse> {
        return Mono.empty()
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> {
        return Mono.empty()
    }

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> {
        return Mono.empty()
    }

    @PatchMapping("/{id}/status")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): Mono<TaskResponse> {
        return Mono.empty()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> {
        return Mono.empty()
    }
}

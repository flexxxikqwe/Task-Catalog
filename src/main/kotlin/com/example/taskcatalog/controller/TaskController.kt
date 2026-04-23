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
        // Skeleton for phase 1
        return Mono.empty()
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> {
        // Skeleton for phase 1
        return Mono.empty()
    }

    @GetMapping
    fun getTasks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> {
        // Skeleton for phase 1
        return Mono.empty()
    }

    @PatchMapping("/{id}/status")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): Mono<TaskResponse> {
        // Skeleton for phase 1
        return Mono.empty()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> {
        // Skeleton for phase 1
        return Mono.empty()
    }
}

package com.example.taskcatalog.service

import com.example.taskcatalog.dto.request.CreateTaskRequest
import com.example.taskcatalog.dto.request.UpdateTaskStatusRequest
import com.example.taskcatalog.dto.response.PageResponse
import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.exception.TaskNotFoundException
import com.example.taskcatalog.mapper.TaskMapper
import com.example.taskcatalog.model.Task
import com.example.taskcatalog.model.TaskStatus
import com.example.taskcatalog.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import kotlin.math.ceil

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val taskMapper: TaskMapper
) : TaskService {

    override fun createTask(request: CreateTaskRequest): Mono<TaskResponse> {
        return Mono.fromCallable {
            val now = LocalDateTime.now()
            val task = Task(
                title = request.title,
                description = request.description,
                status = TaskStatus.NEW,
                createdAt = now,
                updatedAt = now
            )
            taskRepository.save(task)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .map { taskMapper.toResponse(it) }
    }

    override fun getTaskById(id: Long): Mono<TaskResponse> {
        return Mono.fromCallable {
            taskRepository.findById(id)
                ?: throw TaskNotFoundException("Task with id $id not found")
        }
            .subscribeOn(Schedulers.boundedElastic())
            .map { taskMapper.toResponse(it) }
    }

    override fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> {
        val contentMono = Mono.fromCallable {
            taskRepository.findAll(page, size, status)
        }.subscribeOn(Schedulers.boundedElastic())

        val countMono = Mono.fromCallable {
            taskRepository.count(status)
        }.subscribeOn(Schedulers.boundedElastic())

        return Mono.zip(contentMono, countMono) { content, totalElements ->
            val totalPages = if (totalElements == 0L) 0 else ceil(totalElements.toDouble() / size).toInt()
            PageResponse(
                content = content.map { taskMapper.toResponse(it) },
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages
            )
        }
    }

    override fun updateTaskStatus(id: Long, request: UpdateTaskStatusRequest): Mono<TaskResponse> {
        return Mono.fromCallable {
            val updated = taskRepository.updateStatus(id, request.status)
            if (!updated) {
                throw TaskNotFoundException("Task with id $id not found")
            }
            taskRepository.findById(id) ?: throw TaskNotFoundException("Task with id $id not found after update")
        }
            .subscribeOn(Schedulers.boundedElastic())
            .map { taskMapper.toResponse(it) }
    }

    override fun deleteTask(id: Long): Mono<Void> {
        return Mono.fromCallable {
            val deleted = taskRepository.deleteById(id)
            if (!deleted) {
                throw TaskNotFoundException("Task with id $id not found")
            }
        }
            .subscribeOn(Schedulers.boundedElastic())
            .then()
    }
}

package com.example.taskcatalog.service

import com.example.taskcatalog.dto.request.CreateTaskRequest
import com.example.taskcatalog.dto.request.UpdateTaskStatusRequest
import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.exception.TaskNotFoundException
import com.example.taskcatalog.mapper.TaskMapper
import com.example.taskcatalog.model.Task
import com.example.taskcatalog.model.TaskStatus
import com.example.taskcatalog.repository.TaskRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.test.StepVerifier
import java.time.LocalDateTime
import org.mockito.ArgumentMatchers.any as mockitoAny

@ExtendWith(MockitoExtension::class)
class TaskServiceImplTest {

    @Mock
    lateinit var taskRepository: TaskRepository

    @Mock
    lateinit var taskMapper: TaskMapper

    @InjectMocks
    lateinit var taskService: TaskServiceImpl

    private val now = LocalDateTime.now()
    private val task = Task(1L, "Test Task", "Description", TaskStatus.NEW, now, now)
    private val taskResponse = TaskResponse(1L, "Test Task", "Description", TaskStatus.NEW, now, now)

    @Test
    fun `createTask should save and return task response`() {
        val request = CreateTaskRequest("Test Task", "Description")
        `when`(taskRepository.save(mockitoAny(Task::class.java))).thenReturn(task)
        `when`(taskMapper.toResponse(task)).thenReturn(taskResponse)

        StepVerifier.create(taskService.createTask(request))
            .expectNext(taskResponse)
            .verifyComplete()
    }

    @Test
    fun `getTaskById should return task response when task exists`() {
        `when`(taskRepository.findById(1L)).thenReturn(task)
        `when`(taskMapper.toResponse(task)).thenReturn(taskResponse)

        StepVerifier.create(taskService.getTaskById(1L))
            .expectNext(taskResponse)
            .verifyComplete()
    }

    @Test
    fun `getTaskById should throw TaskNotFoundException when task does not exist`() {
        `when`(taskRepository.findById(1L)).thenReturn(null)

        StepVerifier.create(taskService.getTaskById(1L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `getTasks should return paginated response`() {
        `when`(taskRepository.findAll(0, 10, TaskStatus.NEW)).thenReturn(listOf(task))
        `when`(taskRepository.count(TaskStatus.NEW)).thenReturn(1L)
        `when`(taskMapper.toResponse(task)).thenReturn(taskResponse)

        StepVerifier.create(taskService.getTasks(0, 10, TaskStatus.NEW))
            .assertNext { response ->
                assert(response.content.size == 1)
                assert(response.totalElements == 1L)
                assert(response.totalPages == 1)
                assert(response.page == 0)
            }
            .verifyComplete()
    }

    @Test
    fun `updateTaskStatus should update status and return updated task`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        val updatedTask = task.copy(status = TaskStatus.DONE)
        val updatedResponse = taskResponse.copy(status = TaskStatus.DONE)

        `when`(taskRepository.updateStatus(1L, TaskStatus.DONE)).thenReturn(true)
        `when`(taskRepository.findById(1L)).thenReturn(updatedTask)
        `when`(taskMapper.toResponse(updatedTask)).thenReturn(updatedResponse)

        StepVerifier.create(taskService.updateTaskStatus(1L, request))
            .expectNext(updatedResponse)
            .verifyComplete()
    }

    @Test
    fun `deleteTask should complete when deletion is successful`() {
        `when`(taskRepository.deleteById(1L)).thenReturn(true)

        StepVerifier.create(taskService.deleteTask(1L))
            .verifyComplete()
    }

    @Test
    fun `deleteTask should throw exception when task to delete is not found`() {
        `when`(taskRepository.deleteById(1L)).thenReturn(false)

        StepVerifier.create(taskService.deleteTask(1L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }
}

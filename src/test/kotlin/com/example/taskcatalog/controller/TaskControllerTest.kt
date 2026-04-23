package com.example.taskcatalog.controller

import com.example.taskcatalog.dto.request.CreateTaskRequest
import com.example.taskcatalog.dto.request.UpdateTaskStatusRequest
import com.example.taskcatalog.dto.response.PageResponse
import com.example.taskcatalog.dto.response.TaskResponse
import com.example.taskcatalog.exception.GlobalExceptionHandler
import com.example.taskcatalog.exception.TaskNotFoundException
import com.example.taskcatalog.model.TaskStatus
import com.example.taskcatalog.service.TaskService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactivetest.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import org.mockito.ArgumentMatchers.any as mockitoAny

@WebFluxTest(TaskController::class)
@Import(GlobalExceptionHandler::class)
class TaskControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockitoBean
    lateinit var taskService: TaskService

    private val now = LocalDateTime.now()
    private val taskResponse = TaskResponse(1L, "Title", "Desc", TaskStatus.NEW, now, now)

    @Test
    fun `POST createTask returns 201 Created`() {
        val request = CreateTaskRequest("Title", "Desc")
        `when`(taskService.createTask(mockitoAny(CreateTaskRequest::class.java))).thenReturn(Mono.just(taskResponse))

        webClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
    }

    @Test
    fun `POST createTask returns 400 for empty title`() {
        val request = CreateTaskRequest("", "Desc")

        webClient.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET getTaskById returns 200 OK`() {
        `when`(taskService.getTaskById(1L)).thenReturn(Mono.just(taskResponse))

        webClient.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
    }

    @Test
    fun `GET getTaskById returns 404 when not found`() {
        `when`(taskService.getTaskById(1L)).thenReturn(Mono.error(TaskNotFoundException("Task 1 not found")))

        webClient.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `GET getTasks returns 200 OK`() {
        val response = PageResponse(listOf(taskResponse), 0, 10, 1L, 1)
        `when`(taskService.getTasks(anyInt(), anyInt(), mockitoAny(TaskStatus::class.java))).thenReturn(Mono.just(response))

        webClient.get()
            .uri("/api/tasks?page=0&size=10&status=NEW")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content.length()").isEqualTo(1)
    }

    @Test
    fun `GET getTasks returns 400 for invalid page or size`() {
        webClient.get()
            .uri("/api/tasks?page=-1&size=0")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET getTasks returns 400 for invalid status enum`() {
        webClient.get()
            .uri("/api/tasks?page=0&size=10&status=INVALID_STATUS")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `PATCH updateTaskStatus returns 200 OK`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        `when`(taskService.updateTaskStatus(anyLong(), mockitoAny(UpdateTaskStatusRequest::class.java)))
            .thenReturn(Mono.just(taskResponse.copy(status = TaskStatus.DONE)))

        webClient.patch()
            .uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("DONE")
    }

    @Test
    fun `PATCH updateTaskStatus returns 400 for invalid body`() {
        webClient.patch()
            .uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `DELETE deleteTask returns 204 No Content`() {
        `when`(taskService.deleteTask(1L)).thenReturn(Mono.empty())

        webClient.delete()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `DELETE deleteTask returns 404 when task not found`() {
        `when`(taskService.deleteTask(1L)).thenReturn(Mono.error(TaskNotFoundException("Task 1 not found")))

        webClient.delete()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNotFound
    }
}

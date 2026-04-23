# Task Catalog Backend

## Project Overview
Task Catalog is a backend REST service for managing tasks. It allows creating, retrieving, updating status, and deleting tasks.

## Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 3.4.2
- **Reactive Stack**: Spring WebFlux (for the API and Service layers)
- **Database Access**: Spring JDBC `JdbcClient` (synchronous native SQL)
- **Database**: H2 (In-memory for development)
- **Migrations**: Flyway
- **Build System**: Gradle Kotlin DSL
- **Java Version**: 21

## Architecture Overview
The project follows a layered architecture:
- **Controller**: REST API endpoints, handles requests and returns `Mono`/`Flux`.
- **Service**: Business logic, orchestrates repository calls and maps entities to DTOs. Reactor types are used here.
- **Repository**: Data access using synchronous `JdbcClient`.
- **Model**: Domain entities.
- **DTO**: Request and Response data transfer objects.
- **Mapper**: Component for converting between models and DTOs.
- **Exception**: Custom exceptions and a global exception handler.

### Synchronous vs. Reactive
The Repository layer uses the synchronous `JdbcClient` with native SQL for simplicity and explicit control over queries. To integrate this into a reactive WebFlux flow, blocking calls are wrapped in `Mono.fromCallable` or `Mono.fromRunnable` and executed on a `Schedulers.boundedElastic()` thread pool in the Service layer.

## Run Instructions
To run the application:
```bash
./gradlew bootRun
```
The server will start on port 3000.

## API Endpoints
- `POST /api/tasks`: Create a new task.
- `GET /api/tasks/{id}`: Get task details by ID.
- `GET /api/tasks?page=0&size=10&status=NEW`: List tasks with pagination and status filtering.
- `PATCH /api/tasks/{id}/status`: Update task status.
- `DELETE /api/tasks/{id}`: Delete a task.

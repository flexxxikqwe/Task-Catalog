# Task Catalog Backend

## Project Overview
Task Catalog is a backend REST service for managing tasks. It allows creating, retrieving, paginating, and deleting tasks with a focus on performance and explicit database control.

## Technical Stack
- **Language**: Kotlin 1.9
- **Framework**: Spring Boot 3.4.2 (WebFlux)
- **Database Access**: Spring JDBC `JdbcClient` (Native SQL)
- **Database**: H2 (In-memory)
- **Migrations**: Flyway
- **Java Version**: 21
- **Testing**: JUnit 5, Mockito, StepVerifier

## Architecture
The application uses a layered architecture designed for scalability:
- **Reactive Service Layer**: Exposes `Mono` and `Flux` types. It bridges the blocking Repository calls using `Schedulers.boundedElastic()` to keep the WebFlux event loop responsive.
- **Explicit Repository Layer**: Uses `JdbcClient` with native SQL to provide transparent and optimized database access without the complexity of an ORM.
- **Strict Validation**: Leverages `jakarta.validation` at both DTO and Controller levels to ensure data integrity at the edge.

## API Documentation

### Base URL: `/api/tasks`

| Method | Endpoint | Description | Status |
| :--- | :--- | :--- | :--- |
| **POST** | `/` | Create a new task. | 201 |
| **GET** | `/{id}` | Get task details. | 200 / 404 |
| **GET** | `?page=X&size=Y&status=Z` | List tasks (Sorted by created_at DESC). | 200 / 400 |
| **PATCH** | `/{id}/status` | Update task status. | 200 / 404 |
| **DELETE** | `/{id}` | Delete a task. | 204 / 404 |

## Running the Project
```bash
./gradlew bootRun
```
The application starts on port **3000**.

## Running Tests
```bash
./gradlew test
```
The test suite covers Service-tier business logic and Controller-tier validation/status mapping.

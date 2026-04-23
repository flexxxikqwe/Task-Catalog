package com.example.taskcatalog.repository

import com.example.taskcatalog.model.Task
import com.example.taskcatalog.model.TaskStatus
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class JdbcTaskRepository(private val jdbcClient: JdbcClient) : TaskRepository {

    private val columns = "id, title, description, status, created_at, updated_at"

    override fun save(task: Task): Task {
        val keyHolder = GeneratedKeyHolder()

        jdbcClient.sql("""
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
        """)
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("createdAt", task.createdAt)
            .param("updatedAt", task.updatedAt)
            .update(keyHolder)

        val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated ID")
        return task.copy(id = generatedId)
    }

    override fun findById(id: Long): Task? {
        return jdbcClient.sql("SELECT $columns FROM tasks WHERE id = :id")
            .param("id", id)
            .query { rs, _ -> mapRowToTask(rs) }
            .optional()
            .orElse(null)
    }

    override fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
        val sql = StringBuilder("SELECT $columns FROM tasks ")
        if (status != null) {
            sql.append("WHERE status = :status ")
        }
        sql.append("ORDER BY created_at DESC LIMIT :limit OFFSET :offset")

        val query = jdbcClient.sql(sql.toString())
            .param("limit", size)
            .param("offset", page * size)

        if (status != null) {
            query.param("status", status.name)
        }

        return query.query { rs, _ -> mapRowToTask(rs) }.list()
    }

    override fun count(status: TaskStatus?): Long {
        val sql = if (status != null) {
            "SELECT COUNT(*) FROM tasks WHERE status = :status"
        } else {
            "SELECT COUNT(*) FROM tasks"
        }

        val query = jdbcClient.sql(sql)
        if (status != null) {
            query.param("status", status.name)
        }

        return query.query(Long::class.java).single()
    }

    override fun updateStatus(id: Long, status: TaskStatus): Boolean {
        val updatedRows = jdbcClient.sql("""
            UPDATE tasks 
            SET status = :status, updated_at = :updatedAt 
            WHERE id = :id
        """)
            .param("id", id)
            .param("status", status.name)
            .param("updatedAt", LocalDateTime.now())
            .update()

        return updatedRows > 0
    }

    override fun deleteById(id: Long): Boolean {
        val deletedRows = jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id)
            .update()

        return deletedRows > 0
    }

    private fun mapRowToTask(rs: ResultSet): Task {
        return Task(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            status = TaskStatus.valueOf(rs.getString("status")),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}

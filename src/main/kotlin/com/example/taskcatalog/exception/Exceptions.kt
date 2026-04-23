package com.example.taskcatalog.exception

class TaskNotFoundException(message: String) : RuntimeException(message)

class BadRequestException(message: String) : RuntimeException(message)
